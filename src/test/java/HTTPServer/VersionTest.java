package HTTPServer;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {

  @Test
  public void GET_EXACT_RETURNS_CORRECTLY() {
    Version expected = Version.HTTP_1_1;
    int major = expected.getMajorVer();
    int minor = expected.getMinorVer();
    Version actual = Version.getExact(major, minor);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void WRONG_MAJOR_VERSION_RETURNS_NULL() {
    Version expected = null;
    Version actual = Version.getExact(5, 1);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void WRONG_MINOR_VERSION_RETURNS_NULL() {
    Version expected = null;
    Version actual = Version.getExact(1, 5);
    Assert.assertEquals(expected, actual);
  }
}