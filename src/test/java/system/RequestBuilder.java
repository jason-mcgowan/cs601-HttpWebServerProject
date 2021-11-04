package system;

import httpserver.Request;
import httpserver.RequestException;
import httpserver.RequestReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RequestBuilder {
  public static Request genGoodRequest() throws IOException, RequestException {
    return textToRequest(genPostRequestWithBody());
  }

  public static Request genRequestWithMethod(String method, String body)
      throws IOException, RequestException {
    return textToRequest(genPostRequestWithMethod(method, body));
  }

  private static Request textToRequest(String text) throws IOException, RequestException {
    return RequestReader.readRequest(genReader(text));
  }

  private static String genPostRequestWithMethod(String method, String body) {
    return method + " /urlgoeshere?something#there HTTP/1.1\r\n"
        + "Host: localhost:8080\r\n"
        + "Content-length: " + body.length() + "\r\n"
        + "\r\n"
        + body;
  }

  private static String genPostRequestWithBody(String body) {
    return genPostRequestWithMethod("POST", body);
  }

  private static String genPostRequestWithBody() {
    return genPostRequestWithBody("input=blank");
  }

  private static InputStreamReader genReader(String text) {
    return new InputStreamReader(
        new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII)));
  }
}
