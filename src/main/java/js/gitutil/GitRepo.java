package js.gitutil;

import java.io.File;
import java.util.List;
import java.util.Map;

import static js.base.Tools.*;

import js.base.BaseObject;
import js.base.SystemCall;
import js.data.DataUtil;
import js.file.Files;
import gitutil.gen.FileEntry;
import gitutil.gen.FileState;
import js.parsing.StringParser;

public final class GitRepo extends BaseObject {

  public GitRepo(File directory) {
    mRootDirectory = findGitRootDirectory(directory);
  }

  public File rootDirectory() {
    return mRootDirectory;
  }

  public File absoluteFile(String pathRelativeToRoot) {
    checkArgument(nonEmpty(pathRelativeToRoot));
    return new File(rootDirectory(), pathRelativeToRoot);
  }

  public String branchName() {
    if (mBranch == null) {
      // If in detached head mode, this won't work;
      //
      // https://stackoverflow.com/questions/6245570
      //   
      mBranch = "<UNKNOWN>";
      SystemCall s = sysCall().arg("git", "rev-parse", "--abbrev-ref", "HEAD");
      if (s.exitCode() == 0) {
        File f = new File(s.systemOut().trim());
        mBranch = Files.basename(f);
      }
    }
    return mBranch;
  }

  public String past_commit_name() {
    return past_commit_name(-1);
  }

  public String past_commit_name(int index) {
    checkArgument(index < 0, "index must be negative");
    if (mPastCommitNames == null) {
      String x = sysCall().arg("git", "log", "--pretty=format:\"%h\"", "-30").systemOut().trim();
      mPastCommitNames = arrayList();
      for (String w : split(x, '\n')) {
        // Remove quotes "xxxx" from string
        mPastCommitNames.add(w.substring(1, w.length() - 1));
      }
    }
    int j = -1 - index;
    if (j >= mPastCommitNames.size())
      throw badArg("No such commit at index", index);
    return mPastCommitNames.get(j);
  }

  private static final Map<String, FileState> sFileStateMap = hashMap();

  private static void add(String str, FileState state) {
    sFileStateMap.put(str, state);
  }

  static {
    add(" ", FileState.UNMODIFIED);
    add("?", FileState.UNTRACKED);
    add("M", FileState.MODIFIED);
    add("A", FileState.ADDED);
    add("D", FileState.DELETED);
    add("R", FileState.RENAMED);
    add("C", FileState.COPIED);
    add("U", FileState.UNMERGED);
  }

  private static FileState fileState(String text) {
    FileState result = sFileStateMap.get(text);
    if (result == null)
      throw badArg("Unrecognized file state key:", text);
    return result;
  }

  public boolean workingTreeModified() {
    return !fileEntries().isEmpty();
  }

  /**
   * Perform a git status to determine modified, deleted, untracked, and added
   * files
   */
  public List<FileEntry> fileEntries() {
    if (mFileEntries != null)
      return mFileEntries;

    List<FileEntry> out = arrayList();

    // See: https://git-scm.com/docs/git-status#_porcelain_format_version_2

    SystemCall systemCall = sysCall();
    systemCall.arg("git", "status", "--porcelain");
    String content = chomp(systemCall.systemOut());
    if (!content.isEmpty()) {
      for (String line : split(content, '\n')) {
        FileEntry.Builder b = FileEntry.newBuilder();

        StringParser gp = new StringParser(line);
        String status1 = gp.readChars(1);
        String status2 = gp.readChars(1);
        b.oldState(fileState(status1));
        b.state(fileState(status2));

        gp.read(" ");

        String path = gp.readPath();

        if (!gp.done()) {
          gp.read(" -> ");
          b.origPath(path);
          path = gp.readPath();
        }
        b.path(path);

        gp.assertDone();
        out.add(b.build());
      }
    }
    mFileEntries = out;
    return out;
  }

  public List<FileEntry> untrackedFiles() {
    if (mUntracked == null) {
      mUntracked = arrayList();
      for (FileEntry ent : fileEntries()) {
        if (ent.state() == FileState.UNTRACKED)
          mUntracked.add(ent);
      }
    }
    return mUntracked;
  }

  public List<FileEntry> unmergedFiles() {
    if (mUnmerged == null) {
      mUnmerged = arrayList();
      for (FileEntry ent : fileEntries()) {
        if (ent.state() == FileState.UNMERGED)
          mUnmerged.add(ent);
      }
    }
    return mUnmerged;
  }

  public List<FileEntry> markedFiles() {
    if (mMarked == null) {
      mMarked = arrayList();
      for (FileEntry ent : fileEntries()) {
        if (ent.state() == FileState.MODIFIED || ent.state() == FileState.ADDED) {
          File path = absoluteFile(ent.path());
          byte[] bytes = Files.toByteArray(path, null);
          if (indexOfMarker(bytes) >= 0)
            mMarked.add(ent);
        }
      }
    }
    return mMarked;
  }

  /**
   * Express a file that is relative to the repo root directory relative to
   * another directory (or the current directory if null)
   */
  public File fileRelativeToDirectory(String filePath, File directoryOrNull) {
    File otherDirectory = Files.ifEmpty(directoryOrNull, Files.currentDirectory());
    return Files.fileRelativeToDirectory(absoluteFile(filePath), otherDirectory);
  }

  private static File findGitRootDirectory(File descendentDir) {
    descendentDir = Files.absolute(descendentDir);
    Files.assertDirectoryExists(descendentDir);
    File result = descendentDir;
    while (true) {
      File sentinel = new File(result, ".git");
      if (sentinel.exists())
        return result;
      result = result.getParentFile();
      checkState(Files.nonEmpty(result), "No .git directory found in parents of:", descendentDir);
    }
  }

  private SystemCall sysCall() {
    return new SystemCall().withVerbose(verbose());
  }

  // ------------------------------------------------------------------
  // File marking
  // ------------------------------------------------------------------

  public static final String MARK_SENTINEL_TEXT = "*/                            %%% // gitdiff marker";

  private static final byte[] SENTINEL_BYTES = DataUtil.toByteArray(MARK_SENTINEL_TEXT);

  /**
   * Returns the index within a byte array of the first occurrence of the mark
   * string's bytes. If the mark sequence isn't found, rturns -1.
   */
  private int indexOfMarker(byte[] bytes) {
    byte[] needle = SENTINEL_BYTES;
    int charTable[] = MARK_JUMP_TABLE;
    int offsetTable[] = MARK_OFFSET_TABLE;
    for (int i = needle.length - 1, j; i < bytes.length;) {
      for (j = needle.length - 1; needle[j] == bytes[i]; --i, --j) {
        if (j == 0)
          return i;
      }
      int by = bytes[i];
      // If character isn't in the alphabet, act as if it's the last one in the alphabet
      if (by < 0 || by >= ALPHABET_SIZE)
        by = ALPHABET_SIZE - 1;
      i += Math.max(offsetTable[needle.length - 1 - j], charTable[by]);
    }
    return -1;
  }

  /**
   * Makes the jump table based on the mismatched byte information
   */
  private static int[] makeJumpTable(byte[] needle) {
    int[] table = new int[ALPHABET_SIZE];
    for (int i = 0; i < table.length; i++)
      table[i] = needle.length;
    for (int i = 0; i < needle.length - 2; i++)
      table[needle[i]] = needle.length - 1 - i;
    return table;
  }

  /**
   * Makes the jump table based on the scan offset which mismatch occurs
   */
  private static int[] makeOffsetTable(byte[] needle) {
    int[] table = new int[needle.length];
    int lastPrefixPosition = needle.length;
    for (int i = needle.length; i > 0; i--) {
      if (isPrefix(needle, i))
        lastPrefixPosition = i;
      table[needle.length - i] = lastPrefixPosition - i + needle.length;
    }
    for (int i = 0; i < needle.length - 1; i++) {
      int slen = suffixLength(needle, i);
      table[slen] = needle.length - 1 - i + slen;
    }
    return table;
  }

  private static final int ALPHABET_SIZE = 1 + (int) Byte.MAX_VALUE;

  private static final int[] MARK_JUMP_TABLE = makeJumpTable(SENTINEL_BYTES);
  private static final int[] MARK_OFFSET_TABLE = makeOffsetTable(SENTINEL_BYTES);

  /**
   * Is needle[p:end] a prefix of needle?
   */
  private static boolean isPrefix(byte[] needle, int p) {
    for (int i = p, j = 0; i < needle.length; ++i, ++j) {
      if (needle[i] != needle[j])
        return false;
    }
    return true;
  }

  /**
   * Returns the maximum length of the subsequence that ends at p and is a
   * suffix
   */
  private static int suffixLength(byte[] needle, int p) {
    int len = 0;
    for (int i = p, j = needle.length - 1; i >= 0 && needle[i] == needle[j]; i--, j--)
      len += 1;
    return len;
  }

  private final File mRootDirectory;
  private String mBranch;
  private List<String> mPastCommitNames;
  private List<FileEntry> mFileEntries;
  private List<FileEntry> mUntracked;
  private List<FileEntry> mUnmerged;
  private List<FileEntry> mMarked;

}
