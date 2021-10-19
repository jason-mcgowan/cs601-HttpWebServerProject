package HTTPServer;

import java.util.HashMap;

public class Request {
  private RequestLine requestLine;
  private HashMap<String, String> headers;
  private String body;

  @Override
  public String toString() {
    return "Request{" +
        "requestLine=" + requestLine +
        ", headers=" + headers +
        ", body='" + body + '\'' +
        '}';
  }

  public Request(RequestLine requestLine,
      HashMap<String, String> headers, String body) {
    this.requestLine = requestLine;
    this.headers = headers;
    this.body = body;


  }
}
