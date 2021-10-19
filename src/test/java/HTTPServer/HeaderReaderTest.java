package HTTPServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HeaderReaderTest {

  private static final String goodField1 = "Field-1";
  private static final String goodValue1 = "akLjdfl1248907";
  private static final String goodField2 = "num2";
  private static final String goodValue2 = "-%kk jiwu   ansn";
  private static final String CRLF = "\r\n";

  private static HashMap<String, String> headers;

  @Before
  public void setUp() {
    headers = new HashMap<>();
  }

  @Test
  public void VALID_HEADER_RETURNS_AND_PARSES_CORRECTLY() {
    String validHeader = goodField1 + ":" + goodValue1 + CRLF
        + "\n\n\n" + goodField2 + ":     " + goodValue2 + CRLF
        + CRLF;
    assertValidResults(validHeader);
  }

  @Test(expected = IOException.class)
  public void EMPTY_FIELD_THROWS_IOEXCEPTION() throws IOException {
    String header = ": " + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsIOException(header);
  }

  @Test(expected = IOException.class)
  public void LEADING_SPACE_THROWS_IOEXCEPTION() throws IOException {
    String header = " " + goodField1 + ":" + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsIOException(header);
  }

  @Test
  public void LEADING_LINE_FEED_IS_IGNORED() {
    String validHeader = "\n" + goodField1 + ":" + goodValue1 + CRLF
        + goodField2 + ":     " + goodValue2 + CRLF
        + CRLF;
    assertValidResults(validHeader);
  }

  @Test(expected = IOException.class)
  public void SPACE_IN_FIELD_THROWS_IOEXCEPTION() throws IOException {
    String header = "bad field: " + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsIOException(header);
  }

  @Test(expected = IOException.class)
  public void CR_IN_FIELD_THROWS_IOEXCEPTION() throws IOException {
    String header = "bad\rfield: " + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsIOException(header);
  }

  @Test(expected = IOException.class)
  public void LF_IN_FIELD_THROWS_IOEXCEPTION() throws IOException {
    String header = "bad\nfield: " + goodValue1 + CRLF
        + CRLF;
    assertReadingThrowsIOException(header);
  }

  @Test(expected = IOException.class)
  public void LF_IN_VALUE_THROWS_IOEXCEPTION() throws IOException {
    String header = goodField1 + ": this\n isn't allowed" + CRLF
        + CRLF;
    assertReadingThrowsIOException(header);
  }

  @Test(expected = IOException.class)
  public void BAD_LINE_TERM_PATTERN_THROWS_IOEXCEPTION() throws IOException {
    String header = goodField1 + ":" + goodValue1 + "\r \n"
        + CRLF;
    assertReadingThrowsIOException(header);
  }

  private void assertValidResults(String validHeader) {
    try {
      headers = HeaderReader.readHeaderLines(getInputStream(validHeader));
    } catch (IOException e) {
      Assert.fail();
    }
    Assert.assertTrue(headers.containsKey(goodField1.toLowerCase()));
    Assert.assertTrue(headers.containsKey(goodField2.toLowerCase()));
    Assert.assertEquals(goodValue1, headers.get(goodField1.toLowerCase()));
    Assert.assertEquals(goodValue2, headers.get(goodField2.toLowerCase()));
    Assert.assertEquals(2, headers.size());
  }

  private void assertReadingThrowsIOException(String header) throws IOException {
    headers = HeaderReader.readHeaderLines(getInputStream(header));
    Assert.fail();
  }

  private InputStreamReader getInputStream(String text) {
    return new InputStreamReader(
        new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII)));
  }

}