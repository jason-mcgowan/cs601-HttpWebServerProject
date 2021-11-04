package system;

import httpserver.Server;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {

  private static final int PORT = 8080;
  private static final String domain = "localhost";

  private static Server server;

  @Before
  public void setup() {
    server = new Server(domain);
    try {
      server.start(PORT);
    } catch (IOException e) {
      Assert.fail();
    }
  }

  @Test
  public void SERVER_CORRECTLY_HANDLES_GOOD_CONNECTION() {
    // todo
  }

  // todo add remaining tests
}