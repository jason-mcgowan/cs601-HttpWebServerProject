package httpserver.handlers;

import httpserver.Request;
import httpserver.RequestException;
import httpserver.protocol.StatusCode;
import httpserver.util.Responses;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import system.RequestBuilder;

public class ChatHandlerTest {

  private static final int API_PORT = 8080;
  private static final String WEBHOOK = "http://localhost:" + API_PORT + "/";
  private static final String MAP_URL = "/url";
  private static final String MAP_DOMAIN = "domain";
  private static ChatHandler handler;

  @Before
  public void setUp() {
    handler = new ChatHandler(WEBHOOK);
    handler.setUrl(MAP_URL);
    handler.setDomain(MAP_DOMAIN);
  }

  @Test
  public void NON_OKAY_RESPONSE_RETURNS_500() {
    new Thread(() -> mockApiBadResponse(API_PORT)).start();
    try {
      Request request = RequestBuilder.genRequestWithMethod("POST", "msg=blank");
      String response = handler.respond(request);
      String expected = "HTTP/1.1 500";
      String actual = response.substring(0, expected.length());
      Assert.assertEquals(expected, actual);
    } catch (IOException | RequestException e) {
      Assert.fail();
    }
  }

  private void mockApiBadResponse(int port) {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      Socket socket = serverSocket.accept();
      String response = Responses.getStandardErrorResponse(
          new RequestException(StatusCode.CLIENT_ERROR_400_BAD_REQUEST));
      respondToClient(response, socket);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void respondToClient(String response, Socket client) {
    try {
      OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream(),
          StandardCharsets.US_ASCII);
      osw.write(response);
      osw.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}