package HTTPServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;

public class RequestLineReaderTest {

  @Test
  public void VALID_REQUEST_LINE_PARSES_CORRECTLY() {
    String uri = "l;kjwopiurqmz/.nas;l";
    String requestText = "GET " + uri + " HTTP/1.1\r\n";

    try (InputStream is = new ByteArrayInputStream(requestText.getBytes());
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.US_ASCII)) {
      RequestLine request = RequestLineReader.readRequestLine(isr);
      Assert.assertEquals(request.getMethod(), Method.GET);
      Assert.assertEquals(request.getRequestURI(), uri);
      Assert.assertEquals(request.getVersion(), Version.HTTP_1_1);
    } catch (IOException | RequestException e) {
      Assert.fail();
    }
  }

  // region Method Tests
  @Test
  public void METHOD_NAME_NOT_FOUND_THROWS_NOT_IMPLEMENTED() {
    String requestText = "METHOD urihere + HTTP/1.1\r\n";
    assertRequestExceptionThrown(requestText, ResponseCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
  }

  @Test
  public void REQUEST_IS_LONGER_THAN_LONGEST_METHOD_WITHOUT_SPACE_THROWS_BAD_REQUEST() {
    String requestText = "G".repeat(Method.MAX_LENGTH + 1) + " uristuffhere HTTP/1.1\r\n";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void STREAM_ENDS_BEFORE_METHOD_THROWS_BAD_REQUEST() {
    String requestText = "S";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void VALID_METHOD_WITHOUT_SPACE_THROWS_BAD_REQUEST() {
    String requestText = "GET";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void VALID_METHOD_WITH_SPACE_THROWS_BAD_REQUEST() {
    String requestText = "GET ";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void INITIAL_WHITE_SPACE_THROWS_NOT_IMPLEMENTED() {
    String requestText = " " + genParsableRequestLine();
    assertRequestExceptionThrown(requestText, ResponseCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
  }
  // endregion

  // region URI Tests
  @Test
  public void STREAM_ENDS_MID_URI_THROWS_BAD_REQUEST() {
    String requestText = "GET ;lkqjwerup";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void URI_OVER_MAX_LENGTH_THROWS_URI_TOO_LONG() {
    String requestText = "GET " + "x".repeat(2049) + " HTTP/1.1\r\n";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_414_URI_TOO_LONG);
  }

  @Test
  public void URI_WITH_CR_THROWS_BAD_REQUEST() {
    String requestText = "GET uriwith\r HTTP/1.1\r\n";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void URI_WITH_LF_THROWS_BAD_REQUEST() {
    String requestText = "GET uriwith\n HTTP/1.1\r\n";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }
  // endregion

  // region Version Tests
  @Test
  public void VERSION_TOO_SHORT_THROWS_BAD_REQUEST() {
    String requestText = "GET jl;kwerpq HTP/1.1\r\n";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void VERSION_TOO_LONG_THROWS_BAD_REQUEST() {
    String requestText = "GET jl;kwerpq HTTTP/1.1\r\n";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void VERSION_TYPO_THROWS_BAD_REQUEST() {
    String requestText = "GET jl;kwerpq HTtP/1.1\r\n";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void INCORRECT_MAJOR_VERSION_THROWS_NOT_SUPPORTED() {
    String requestText = "GET jl;kwerpq HTTP/2.1\r\n";
    assertRequestExceptionThrown(requestText,
        ResponseCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
  }

  @Test
  public void INCORRECT_MINOR_VERSION_THROWS_NOT_SUPPORTED() {
    String requestText = "GET jl;kwerpq HTTP/1.0\r\n";
    assertRequestExceptionThrown(requestText,
        ResponseCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
  }
  // endregion

  // region Line End Tests
  @Test
  public void NO_CRLF_AT_END_THROWS_BAD_REQUEST() {
    String requestText = "GET jl;kwerpq HTTP/1.1";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void NO_CR_THROWS_BAD_REQUEST() {
    String requestText = "GET jl;kwerpq HTTP/1.1\n";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void NO_LF_THROWS_BAD_REQUEST() {
    String requestText = "GET jl;kwerpq HTTP/1.1\r";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  @Test
  public void LFCR_AT_END_THROWS_BAD_REQUEST() {
    String requestText = "GET jl;kwerpq HTTP/1.1\n\r";
    assertRequestExceptionThrown(requestText, ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
  }
  // endregion

  private String genParsableRequestLine() {
    return "GET uriGibberishHere HTTP/1.1\r\n";
  }

  private void assertRequestExceptionThrown(String requestText, ResponseCode expected) {
    try (InputStream is = new ByteArrayInputStream(requestText.getBytes());
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.US_ASCII)) {
      RequestLine request = RequestLineReader.readRequestLine(isr);
      Assert.fail();
    } catch (IOException e) {
      Assert.fail();
    } catch (RequestException e) {
      Assert.assertEquals(expected, e.getResponseCode());
    }
  }
}