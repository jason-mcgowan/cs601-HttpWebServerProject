package httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {

  private static final String domain = "localhost";
  private static int port;

  @Before
  public synchronized void setup() {
    try {
      ServerSocket socket = new ServerSocket(0);
      port = socket.getLocalPort();
      socket.close();
      Server server = new Server(domain);
      server.start(port);
    } catch (IOException e) {
      Assert.fail();
    }
  }

  @Test
  public void NO_URL_MAPPED_THROWS_404_NOT_FOUND() {
    URI uri = URI.create("http://" + domain + ":" + port + "/jkdjka");
    HttpRequest request = HttpRequest.newBuilder(uri)
        .build();
    HttpClient client = HttpClient.newHttpClient();
    try {
      HttpResponse<String> response = client.send(request,
          BodyHandlers.ofString(StandardCharsets.US_ASCII));
      int expected = 404;
      int actual = response.statusCode();
      Assert.assertEquals(expected, actual);
    } catch (IOException | InterruptedException e) {
      Assert.fail();
    }
  }
}