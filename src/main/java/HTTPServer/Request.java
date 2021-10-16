package HTTPServer;

public class Request {

  private Method method;
  private String requestURI;
  private Version version;

  public Request(Method method, String requestURI, Version version) {
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
