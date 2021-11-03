package httpserver.protocol;

/**
 * HTTP request method names. {@see // https://www.rfc-editor.org/rfc/rfc7231.html}
 *
 * @author Jason McGowan
 */
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

  private final String id;

  Method(String id) {
    this.id = id;
  }

  static {
    int longest = 0;
    for (Method method : Method.values()) {
      longest = Math.max(method.id.length(), longest);
    }
    MAX_LENGTH = longest;
  }

  public static Method getExact(String name) {
    for (Method method : Method.values()) {
      if (method.id.equals(name)) {
        return method;
      }
    }
    return null;
  }

  public String getId() {
    return id;
  }
}
