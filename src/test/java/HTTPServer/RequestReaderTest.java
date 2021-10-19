package HTTPServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;

public class RequestReaderTest {

  @Test
  public void VALID_REQUEST_WITH_BODY_READ_CORRECTLY() {
    String body = "query=term";
    try {
      Request request = RequestReader.readRequest(genReader(getValidRequest(body)));
      Assert.assertEquals(body, request.getBody());
    } catch (IOException | RequestException e) {
      Assert.fail();
    }
  }

  private InputStreamReader genReader(String text) {
    return new InputStreamReader(
        new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII)));
  }


  private static String getValidRequest(String body) {
    return "POST /urlgoeshere?something#there HTTP/1.1\r\n"
        + "Host: localhost:8080\r\n"
        + "Content-length: " + body.length() + "\r\n"
        + "\r\n"
        + body;
  }

}