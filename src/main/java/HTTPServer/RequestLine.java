package HTTPServer;

public class RequestLine {

  private final Method method;
  private final String requestURI;
  private final Version version;

  public RequestLine(Method method, String requestURI, Version version) {
    this.method = method;
    this.requestURI = requestURI;
    this.version = version;
  }

  public Method getMethod() {
    return method;
  }

  public String getRequestURI() {
    return requestURI;
  }

  public Version getVersion() {
    return version;
  }
}
