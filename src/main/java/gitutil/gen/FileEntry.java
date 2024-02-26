package gitutil.gen;

import java.util.List;
import js.data.AbstractData;
import js.data.DataUtil;
import js.json.JSList;
import js.json.JSMap;

public class FileEntry implements AbstractData {

  public List<Hunk> hunks() {
    return mHunks;
  }

  public FileState oldState() {
    return mOldState;
  }

  public FileState state() {
    return mState;
  }

  public String path() {
    return mPath;
  }

  public String origPath() {
    return mOrigPath;
  }

  public String oldMode() {
    return mOldMode;
  }

  public String mode() {
    return mMode;
  }

  @Override
  public Builder toBuilder() {
    return new Builder(this);
  }

  public static final String HUNKS = "hunks";
  public static final String OLD_STATE = "old_state";
  public static final String STATE = "state";
  public static final String PATH = "path";
  public static final String ORIG_PATH = "orig_path";
  public static final String OLD_MODE = "old_mode";
  public static final String MODE = "mode";

  @Override
  public String toString() {
    return toJson().prettyPrint();
  }

  @Override
  public JSMap toJson() {
    JSMap m = new JSMap();
    {
      JSList j = new JSList();
      for (Hunk x : mHunks)
        j.add(x.toJson());
      m.put(HUNKS, j);
    }
    m.put(OLD_STATE, mOldState.toString().toLowerCase());
    m.put(STATE, mState.toString().toLowerCase());
    m.put(PATH, mPath);
    m.put(ORIG_PATH, mOrigPath);
    m.put(OLD_MODE, mOldMode);
    m.put(MODE, mMode);
    return m;
  }

  @Override
  public FileEntry build() {
    return this;
  }

  @Override
  public FileEntry parse(Object obj) {
    return new FileEntry((JSMap) obj);
  }

  private FileEntry(JSMap m) {
    mHunks = DataUtil.parseListOfObjects(Hunk.DEFAULT_INSTANCE, m.optJSList(HUNKS), false);
    {
      String x = m.opt(OLD_STATE, "");
      mOldState = x.isEmpty() ? FileState.DEFAULT_INSTANCE : FileState.valueOf(x.toUpperCase());
    }
    {
      String x = m.opt(STATE, "");
      mState = x.isEmpty() ? FileState.DEFAULT_INSTANCE : FileState.valueOf(x.toUpperCase());
    }
    mPath = m.opt(PATH, "");
    mOrigPath = m.opt(ORIG_PATH, "");
    mOldMode = m.opt(OLD_MODE, "");
    mMode = m.opt(MODE, "");
  }

  public static Builder newBuilder() {
    return new Builder(DEFAULT_INSTANCE);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object)
      return true;
    if (object == null || !(object instanceof FileEntry))
      return false;
    FileEntry other = (FileEntry) object;
    if (other.hashCode() != hashCode())
      return false;
    if (!(mHunks.equals(other.mHunks)))
      return false;
    if (!(mOldState.equals(other.mOldState)))
      return false;
    if (!(mState.equals(other.mState)))
      return false;
    if (!(mPath.equals(other.mPath)))
      return false;
    if (!(mOrigPath.equals(other.mOrigPath)))
      return false;
    if (!(mOldMode.equals(other.mOldMode)))
      return false;
    if (!(mMode.equals(other.mMode)))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int r = m__hashcode;
    if (r == 0) {
      r = 1;
      for (Hunk x : mHunks)
        if (x != null)
          r = r * 37 + x.hashCode();
      r = r * 37 + mOldState.ordinal();
      r = r * 37 + mState.ordinal();
      r = r * 37 + mPath.hashCode();
      r = r * 37 + mOrigPath.hashCode();
      r = r * 37 + mOldMode.hashCode();
      r = r * 37 + mMode.hashCode();
      m__hashcode = r;
    }
    return r;
  }

  protected List<Hunk> mHunks;
  protected FileState mOldState;
  protected FileState mState;
  protected String mPath;
  protected String mOrigPath;
  protected String mOldMode;
  protected String mMode;
  protected int m__hashcode;

  public static final class Builder extends FileEntry {

    private Builder(FileEntry m) {
      mHunks = DataUtil.mutableCopyOf(m.mHunks);
      mOldState = m.mOldState;
      mState = m.mState;
      mPath = m.mPath;
      mOrigPath = m.mOrigPath;
      mOldMode = m.mOldMode;
      mMode = m.mMode;
    }

    @Override
    public Builder toBuilder() {
      return this;
    }

    @Override
    public int hashCode() {
      m__hashcode = 0;
      return super.hashCode();
    }

    @Override
    public FileEntry build() {
      FileEntry r = new FileEntry();
      r.mHunks = DataUtil.immutableCopyOf(mHunks);
      r.mOldState = mOldState;
      r.mState = mState;
      r.mPath = mPath;
      r.mOrigPath = mOrigPath;
      r.mOldMode = mOldMode;
      r.mMode = mMode;
      return r;
    }

    public Builder hunks(List<Hunk> x) {
      mHunks = DataUtil.mutableCopyOf((x == null) ? DataUtil.emptyList() : x);
      return this;
    }

    public Builder oldState(FileState x) {
      mOldState = (x == null) ? FileState.DEFAULT_INSTANCE : x;
      return this;
    }

    public Builder state(FileState x) {
      mState = (x == null) ? FileState.DEFAULT_INSTANCE : x;
      return this;
    }

    public Builder path(String x) {
      mPath = (x == null) ? "" : x;
      return this;
    }

    public Builder origPath(String x) {
      mOrigPath = (x == null) ? "" : x;
      return this;
    }

    public Builder oldMode(String x) {
      mOldMode = (x == null) ? "" : x;
      return this;
    }

    public Builder mode(String x) {
      mMode = (x == null) ? "" : x;
      return this;
    }

  }

  public static final FileEntry DEFAULT_INSTANCE = new FileEntry();

  private FileEntry() {
    mHunks = DataUtil.emptyList();
    mOldState = FileState.DEFAULT_INSTANCE;
    mState = FileState.DEFAULT_INSTANCE;
    mPath = "";
    mOrigPath = "";
    mOldMode = "";
    mMode = "";
  }

}
