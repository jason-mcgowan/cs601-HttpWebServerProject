package httpserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.apache.logging.log4j.util.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HeaderReaderTest {

  private static final String goodField1 = "Field-1";
  private static final String goodValue1 = "akLjdfl1248907";
  private static final String goodField2 = "num2";
  private static final String goodValue2 = "-%kk jiwu   ansn";
  private static final String CRLF = "\r\n";
  private static final int HEADER_LENGTH_LIMIT = 16384;

  private static HashMap<String, String> headers;

  @Before
  public void setUp() {
    headers = new HashMap<>();
  }

  @Test
  public void VALID_HEADER_RETURNS_AND_PARSES_CORRECTLY() {
    String validHeader = getComplexHeader();
    assertValidResults(validHeader);
  }

  @Test
  public void EMPTY_FIELD_THROWS_BAD_REQUEST() {
    String header = ": " + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsBadRequest(header);
  }

  @Test
  public void LEADING_SPACE_THROWS_BAD_REQUEST() {
    String header = " " + goodField1 + ":" + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsBadRequest(header);
  }

  @Test
  public void LEADING_LINE_FEED_IS_IGNORED() {
    String validHeader = "\n" + getComplexHeader();
    assertValidResults(validHeader);
  }

  @Test
  public void SPACE_IN_FIELD_THROWS_BAD_REQUEST() {
    String header = "bad field: " + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsBadRequest(header);
  }

  @Test
  public void CR_IN_FIELD_THROWS_BAD_REQUEST() {
    String header = "bad\rfield: " + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsBadRequest(header);
  }

  @Test
  public void LF_IN_FIELD_THROWS_BAD_REQUEST() {
    String header = "bad\nfield: " + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsBadRequest(header);
  }

  @Test
  public void LF_IN_VALUE_THROWS_BAD_REQUEST() {
    String header = goodField1 + ": this\n isn't allowed" + CRLF
        + CRLF;
    assertReadingThrowsBadRequest(header);
  }

  @Test
  public void BAD_LINE_TERM_PATTERN_THROWS_BAD_REQUEST() {
    String header = goodField1 + ":" + goodValue1 + "\r \n"
        + CRLF;
    assertReadingThrowsBadRequest(header);
  }

  @Test
  public void LONG_HEADER_THROWS_PAYLOAD_TOO_LARGE() {
    String header = Strings.repeat("a", HEADER_LENGTH_LIMIT + 1);
    assertReadingThrowsError(header, StatusCode.CLIENT_ERROR_413_PAYLOAD_TOO_LARGE);
  }

  private void assertReadingThrowsError(String header, StatusCode statusCode) {
    try {
      headers = HeaderReader.readHeaderLines(getInputStream(header));
      Assert.fail();
    } catch (IOException e) {
      Assert.fail();
    } catch (RequestException e) {
      Assert.assertEquals(statusCode, e.getResponseCode());
    }
  }

  private String getComplexHeader() {
    return goodField1 + ":" + goodValue1 + CRLF
        + "\n\n\n" + goodField2 + ":     " + goodValue2 + CRLF
        + goodField1 + ":" + goodValue2 + CRLF
        + CRLF;
  }

  private void assertValidResults(String validHeader) {
    try {
      headers = HeaderReader.readHeaderLines(getInputStream(validHeader));
    } catch (IOException | RequestException e) {
      Assert.fail();
    }
    Assert.assertTrue(headers.containsKey(goodField1.toLowerCase()));
    Assert.assertTrue(headers.containsKey(goodField2.toLowerCase()));
    String expectedValue1 = goodValue1 + "," + goodValue2;
    Assert.assertEquals(expectedValue1, headers.get(goodField1.toLowerCase()));
    Assert.assertEquals(goodValue2, headers.get(goodField2.toLowerCase()));
    Assert.assertEquals(2, headers.size());
  }

  private void assertReadingThrowsBadRequest(String header) {
    assertReadingThrowsError(header, StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
  }

  private InputStreamReader getInputStream(String text) {
    return new InputStreamReader(
        new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII)));
  }
}
