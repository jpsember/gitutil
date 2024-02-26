package gitutil.gen;

public enum FileState {

  UNMODIFIED, MODIFIED, ADDED, DELETED, RENAMED, COPIED, UNMERGED, UNTRACKED;

  public static final FileState DEFAULT_INSTANCE = UNMODIFIED;

}
