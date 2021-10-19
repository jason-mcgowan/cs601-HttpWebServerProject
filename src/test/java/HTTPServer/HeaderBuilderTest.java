package HTTPServer;

import java.io.IOException;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;

public class HeaderBuilderTest {


  private static final String goodField1 = "Field-1";
  private static final String goodValue1 = "akLjdfl1248907";
  private static final String goodField2 = "num2";
  private static final String goodValue2 = "-%kk jiwu   ansn";
  private static final String CRLF = "\r\n";

  @Test
  public void VALID_HEADER_RETURNS_AND_PARSES_CORRECTLY() {
    String validHeader = goodField1 + ":" + goodValue1 + CRLF
        + goodField2 + ":     " + goodValue2 + CRLF
        + CRLF;

    HeaderBuilder hb = new HeaderBuilder();
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
}