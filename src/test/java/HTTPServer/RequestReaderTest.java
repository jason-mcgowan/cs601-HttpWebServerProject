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
      Request request = RequestReader.readRequest(genReader(genRequestWithBody(body)));
      Assert.assertEquals(body, request.getBody());
    } catch (IOException | RequestException e) {
      Assert.fail();
    }
  }

  @Test
  public void REQUEST_WITHOUT_BODY_RETURNS_CORRECTLY() {
    try {
      Request request = RequestReader.readRequest(genReader(genRequestNoBody()));
      Assert.assertNull(request.getBody());
    } catch (IOException | RequestException e) {
      Assert.fail();
    }
  }

  @Test
  public void REQUEST_WITH_BODY_AND_TRANSFER_ENCODING_RETURNS_NOT_IMPLEMENTED() {
    try {
      Request request = RequestReader.readRequest(genReader(genRequestTransferEncoding()));
      Assert.fail();
    } catch (IOException e) {
      Assert.fail();
    } catch (RequestException e) {
      Assert.assertEquals(StatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, e.getResponseCode());
    }
  }

  private String genRequestNoBody() {
    return "GET /urlgoeshere?something#there HTTP/1.1\r\n"
        + "Host: localhost:8080\r\n"
        + "\r\n";
  }

  private String genRequestTransferEncoding() {
    return "POST /urlgoeshere?something#there HTTP/1.1\r\n"
        + "Host: localhost:8080\r\n"
        + "Transfer-Encoding: chunked\r\n"
        + "\r\n"
        + (char)5 + "\r\n"
        + "abcde";
  }

  private InputStreamReader genReader(String text) {
    return new InputStreamReader(
        new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII)));
  }

  private String genRequestWithBody(String body) {
    return "POST /urlgoeshere?something#there HTTP/1.1\r\n"
        + "Host: localhost:8080\r\n"
        + "Content-length: " + body.length() + "\r\n"
        + "\r\n"
        + body;
  }
}