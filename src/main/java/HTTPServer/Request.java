package HTTPServer;

import java.util.HashMap;

public class Request {

  private final RequestLine requestLine;
  private final HashMap<String, String> headers;
  private final String body;

  public Request(RequestLine requestLine,
      HashMap<String, String> headers, String body) {
    this.requestLine = requestLine;
    this.headers = headers;
    this.body = body;


  }

  @Override
  public String toString() {
    return "Request{" +
        "requestLine=" + requestLine +
        ", headers=" + headers +
        ", body='" + body + '\'' +
        '}';
  }

  public RequestLine getRequestLine() {
    return requestLine;
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }

  public String getBody() {
    return body;
  }
}
