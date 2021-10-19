package HTTPServer;

import java.io.IOException;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HeaderBuilderTest {


  private static final String goodField1 = "Field-1";
  private static final String goodValue1 = "akLjdfl1248907";
  private static final String goodField2 = "num2";
  private static final String goodValue2 = "-%kk jiwu   ansn";
  private static final String CRLF = "\r\n";

  private static HeaderBuilder hb;

  @Before
  public void setUp() {
    hb = new HeaderBuilder();
  }

  @Test
  public void VALID_HEADER_RETURNS_AND_PARSES_CORRECTLY() {
    String validHeader = goodField1 + ":" + goodValue1 + CRLF
        + "\n\n\n" + goodField2 + ":     " + goodValue2 + CRLF
        + CRLF;

    verifyValidResult(validHeader);
  }

  private void verifyValidResult(String validHeader) {
    boolean check;
    try {
      for (int i = 0; i < validHeader.length(); i++) {
        char c = validHeader.charAt(i);
        check = hb.continueReading(c);
        if (i < validHeader.length() - 1) {
          Assert.assertTrue(check);
        } else {
          Assert.assertFalse(check);
        }
      }
    } catch (IOException e) {
      Assert.fail();
    }

    HashMap<String, String> headers = hb.getHeaders();
    Assert.assertTrue(headers.containsKey(goodField1));
    Assert.assertTrue(headers.containsKey(goodField2));
    Assert.assertEquals(goodValue1, headers.get(goodField1));
    Assert.assertEquals(goodValue2, headers.get(goodField2));
    Assert.assertEquals(2, headers.size());
  }

  @Test(expected = IOException.class)
  public void EMPTY_FIELD_THROWS_IOEXCEPTION() throws IOException {
    String header = ": " + goodValue1 + CRLF
        + CRLF;
    hb.continueReading(header.charAt(0));
    Assert.fail();
  }

  @Test(expected = IOException.class)
  public void LEADING_SPACE_THROWS_IOEXCEPTION() throws IOException {
    String header = " " + goodField1 + ":" + goodValue1 + CRLF
        + CRLF;
    hb.continueReading(header.charAt(0));
    Assert.fail();
  }

  @Test
  public void LEADING_LINE_FEED_IS_IGNORED() {
    String validHeader = "\n" + goodField1 + ":" + goodValue1 + CRLF
        + goodField2 + ":     " + goodValue2 + CRLF
        + CRLF;

    verifyValidResult(validHeader);
  }

  @Test(expected = IOException.class)
  public void SPACE_IN_FIELD_THROWS_IOEXCEPTION() throws IOException {
    String header = "bad field: " + goodValue1 + CRLF
        + CRLF;
    readThroughHeader(header);
  }

  @Test(expected = IOException.class)
  public void CR_IN_FIELD_THROWS_IOEXCEPTION() throws IOException {
    String header = "bad\rfield: " + goodValue1 + CRLF
        + CRLF;
    readThroughHeader(header);
  }

  @Test(expected = IOException.class)
  public void LF_IN_FIELD_THROWS_IOEXCEPTION() throws IOException {
    String header = "bad\nfield: " + goodValue1 + CRLF
        + CRLF;
    readThroughHeader(header);
  }

  @Test(expected = IOException.class)
  public void LF_IN_VALUE_THROWS_IOEXCEPTION() throws IOException {
    String header = goodField1 + ": this\n isn't allowed" + CRLF
        + CRLF;
    readThroughHeader(header);
  }

  @Test(expected = IOException.class)
  public void COLON_IN_VALUE_THROWS_IOEXCEPTION() throws IOException {
    String header = goodField1 + ": this : isn't allowed" + CRLF
        + CRLF;
    readThroughHeader(header);
  }

  @Test(expected = IOException.class)
  public void BAD_LINE_TERM_PATTERN_THROWS_IOEXCEPTION() throws IOException {
    String header = goodField1 + ":" + goodValue1 + "\r \n"
        + CRLF;
    readThroughHeader(header);
  }

  private void readThroughHeader(String header) throws IOException {
    for (char c : header.toCharArray()) {
      hb.continueReading(c);
    }
  }
}