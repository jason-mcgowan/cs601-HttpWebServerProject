package HTTPServer;

public enum Version {
  HTTP_1_1("HTTP/1.1");

  private final String id;

  Version(String id) {
    this.id = id;
  }

  public static Version getExact(String id) {
    for (Version version : Version.values()) {
      if (version.id.equals(id)) {
        return version;
      }
    }
    return null;
  }
}
