package httpserver.protocol;

public enum Version {
  HTTP_1_1(1, 1);

  private final int majorVer;
  private final int minorVer;

  Version(int majorVer, int minorVer) {
    this.majorVer = majorVer;
    this.minorVer = minorVer;
  }

  public static Version getExact(int majorVer, int minorVer) {
    for (Version version : Version.values()) {
      if (version.majorVer == majorVer && version.minorVer == minorVer) {
        return version;
      }
    }
    return null;
  }

  public int getMajorVer() {
    return majorVer;
  }

  public int getMinorVer() {
    return minorVer;
  }
}
