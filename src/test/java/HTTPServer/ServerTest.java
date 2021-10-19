package HTTPServer;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {

  private static final int PORT = 8080;
  private static final String LOCAL_URL = "localhost";

  @Test
  public void SERVER_CORRECTLY_HANDLES_GOOD_CONNECTION() {
    Server server = new Server();
    server.start(PORT);
    try {
      URL url = new URL(LOCAL_URL + ":" + PORT);

    } catch (MalformedURLException e) {
      Assert.fail();
    }
  }

}