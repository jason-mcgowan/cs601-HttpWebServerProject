package HTTPServer;

// https://www.rfc-editor.org/rfc/rfc7231.html#section-4
public enum Method {
  GET("GET"),
  HEAD("HEAD"),
  POST("POST"),
  PUT("PUT"),
  DELETE("DELETE"),
  CONNECT("CONNECT"),
  OPTIONS("OPTIONS"),
  TRACE("TRACE");

  public final static int MAX_LENGTH;

  private final String name;

  Method(String name) {
    this.name = name;
  }

  static {
    int longest = 0;
    for (Method method : Method.values()) {
      longest = Math.max(method.name.length(), longest);
    }
    MAX_LENGTH = longest;
  }

  public static Method getExact(String name) {
    for (Method method : Method.values()) {
      if (method.name.equals(name)) {
        return method;
      }
    }
    return null;
  }
}
