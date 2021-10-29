package httpserver;

import httpserver.protocol.Method;
import org.junit.Assert;
import org.junit.Test;

public class MethodTest {

  @Test
  public void GET_EXACT_RETURNS_CORRECTLY() {
    Method expected = Method.CONNECT;
    String name = expected.name();
    Method actual = Method.getExact(name);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void GET_EXACT_BAD_NAME_RETURNS_NULL() {
    Method expected = null;
    String name = "Gibberish";
    Method actual = Method.getExact(name);
    Assert.assertEquals(expected, actual);
  }
}