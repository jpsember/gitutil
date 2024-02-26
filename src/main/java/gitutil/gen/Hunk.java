package gitutil.gen;

import java.util.List;
import js.data.AbstractData;
import js.data.DataUtil;
import js.json.JSList;
import js.json.JSMap;

public class Hunk implements AbstractData {

  public String filename() {
    return mFilename;
  }

  public int r1Begin() {
    return mR1Begin;
  }

  public int r1Count() {
    return mR1Count;
  }

  public int r2Begin() {
    return mR2Begin;
  }

  public int r2Count() {
    return mR2Count;
  }

  public List<String> lines() {
    return mLines;
  }

  public boolean missingNewline1() {
    return mMissingNewline1;
  }

  public boolean missingNewline2() {
    return mMissingNewline2;
  }

  @Override
  public Builder toBuilder() {
    return new Builder(this);
  }

  public static final String FILENAME = "filename";
  public static final String R1_BEGIN = "r1_begin";
  public static final String R1_COUNT = "r1_count";
  public static final String R2_BEGIN = "r2_begin";
  public static final String R2_COUNT = "r2_count";
  public static final String LINES = "lines";
  public static final String MISSING_NEWLINE1 = "missing_newline1";
  public static final String MISSING_NEWLINE2 = "missing_newline2";

  @Override
  public String toString() {
    return toJson().prettyPrint();
  }

  @Override
  public JSMap toJson() {
    JSMap m = new JSMap();
    m.put(FILENAME, mFilename);
    m.put(R1_BEGIN, mR1Begin);
    m.put(R1_COUNT, mR1Count);
    m.put(R2_BEGIN, mR2Begin);
    m.put(R2_COUNT, mR2Count);
    {
      JSList j = new JSList();
      for (String x : mLines)
        j.add(x);
      m.put(LINES, j);
    }
    m.put(MISSING_NEWLINE1, mMissingNewline1);
    m.put(MISSING_NEWLINE2, mMissingNewline2);
    return m;
  }

  @Override
  public Hunk build() {
    return this;
  }

  @Override
  public Hunk parse(Object obj) {
    return new Hunk((JSMap) obj);
  }

  private Hunk(JSMap m) {
    mFilename = m.opt(FILENAME, "");
    mR1Begin = m.opt(R1_BEGIN, 0);
    mR1Count = m.opt(R1_COUNT, 0);
    mR2Begin = m.opt(R2_BEGIN, 0);
    mR2Count = m.opt(R2_COUNT, 0);
    mLines = DataUtil.parseListOfObjects(m.optJSList(LINES), false);
    mMissingNewline1 = m.opt(MISSING_NEWLINE1, false);
    mMissingNewline2 = m.opt(MISSING_NEWLINE2, false);
  }

  public static Builder newBuilder() {
    return new Builder(DEFAULT_INSTANCE);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object)
      return true;
    if (object == null || !(object instanceof Hunk))
      return false;
    Hunk other = (Hunk) object;
    if (other.hashCode() != hashCode())
      return false;
    if (!(mFilename.equals(other.mFilename)))
      return false;
    if (!(mR1Begin == other.mR1Begin))
      return false;
    if (!(mR1Count == other.mR1Count))
      return false;
    if (!(mR2Begin == other.mR2Begin))
      return false;
    if (!(mR2Count == other.mR2Count))
      return false;
    if (!(mLines.equals(other.mLines)))
      return false;
    if (!(mMissingNewline1 == other.mMissingNewline1))
      return false;
    if (!(mMissingNewline2 == other.mMissingNewline2))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int r = m__hashcode;
    if (r == 0) {
      r = 1;
      r = r * 37 + mFilename.hashCode();
      r = r * 37 + mR1Begin;
      r = r * 37 + mR1Count;
      r = r * 37 + mR2Begin;
      r = r * 37 + mR2Count;
      for (String x : mLines)
        if (x != null)
          r = r * 37 + x.hashCode();
      r = r * 37 + (mMissingNewline1 ? 1 : 0);
      r = r * 37 + (mMissingNewline2 ? 1 : 0);
      m__hashcode = r;
    }
    return r;
  }

  protected String mFilename;
  protected int mR1Begin;
  protected int mR1Count;
  protected int mR2Begin;
  protected int mR2Count;
  protected List<String> mLines;
  protected boolean mMissingNewline1;
  protected boolean mMissingNewline2;
  protected int m__hashcode;

  public static final class Builder extends Hunk {

    private Builder(Hunk m) {
      mFilename = m.mFilename;
      mR1Begin = m.mR1Begin;
      mR1Count = m.mR1Count;
      mR2Begin = m.mR2Begin;
      mR2Count = m.mR2Count;
      mLines = DataUtil.mutableCopyOf(m.mLines);
      mMissingNewline1 = m.mMissingNewline1;
      mMissingNewline2 = m.mMissingNewline2;
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
    public Hunk build() {
      Hunk r = new Hunk();
      r.mFilename = mFilename;
      r.mR1Begin = mR1Begin;
      r.mR1Count = mR1Count;
      r.mR2Begin = mR2Begin;
      r.mR2Count = mR2Count;
      r.mLines = DataUtil.immutableCopyOf(mLines);
      r.mMissingNewline1 = mMissingNewline1;
      r.mMissingNewline2 = mMissingNewline2;
      return r;
    }

    public Builder filename(String x) {
      mFilename = (x == null) ? "" : x;
      return this;
    }

    public Builder r1Begin(int x) {
      mR1Begin = x;
      return this;
    }

    public Builder r1Count(int x) {
      mR1Count = x;
      return this;
    }

    public Builder r2Begin(int x) {
      mR2Begin = x;
      return this;
    }

    public Builder r2Count(int x) {
      mR2Count = x;
      return this;
    }

    public Builder lines(List<String> x) {
      mLines = DataUtil.mutableCopyOf((x == null) ? DataUtil.emptyList() : x);
      return this;
    }

    public Builder missingNewline1(boolean x) {
      mMissingNewline1 = x;
      return this;
    }

    public Builder missingNewline2(boolean x) {
      mMissingNewline2 = x;
      return this;
    }

  }

  public static final Hunk DEFAULT_INSTANCE = new Hunk();

  private Hunk() {
    mFilename = "";
    mLines = DataUtil.emptyList();
  }

}
