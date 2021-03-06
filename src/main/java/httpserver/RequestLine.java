package httpserver;

import httpserver.protocol.Method;
import httpserver.protocol.Version;

/**
 * Represents the Start Line of an HTTP request, the Request Line. Contains the Method, URI, and
 * Version.
 *
 * @author Jason McGowan
 */
public class RequestLine {

  private final Method method;
  private final String requestURI;
  private final Version version;

  public RequestLine(Method method, String requestURI, Version version) {
    this.method = method;
    this.requestURI = requestURI;
    this.version = version;
  }

  @Override
  public String toString() {
    return "RequestLine{" +
        "method=" + method +
        ", requestURI='" + requestURI + '\'' +
        ", version=" + version +
        '}';
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
