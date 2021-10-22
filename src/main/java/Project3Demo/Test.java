package Project3Demo;

import httpserver.Server;
import httpserver.handlers.ReviewSearchHandler;
import httpserver.util.HtmlBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Test {

  public static void main(String[] args)
      throws URISyntaxException, IOException, InterruptedException {
    testServer();
  }

  private static void testServer() {
    Server server = new Server();
    server.addMapping("/reviewsearch", new ReviewSearchHandler());
    server.start(8080);


//    URI uri = new URI("http://localhost:8080/");
////    URI uri = new URI("http://www.google.com/");
//    HttpRequest request = HttpRequest.newBuilder()
//        .uri(uri)
//        .POST(BodyPublishers.ofString("query=term"))
//        .version(HttpClient.Version.HTTP_1_1)
//        .build();
//
//    HttpClient client = HttpClient.newHttpClient();
////    CompletableFuture<HttpResponse<Stream<String>>> response = client.sendAsync(request,
////        BodyHandlers.ofLines());
//    HttpResponse<String> response = client.send(request,
//        BodyHandlers.ofString(StandardCharsets.US_ASCII));
//    System.out.println("Received response");
//    System.out.println(getResponseDetails(response));

//    getGoogleResponse();
  }

  private static void getGoogleResponse() throws IOException {
    try (Socket socket = new Socket("www.google.com", 80)) {

      socket.getOutputStream().write(googleGetRequest().getBytes(StandardCharsets.UTF_8));
      InputStreamReader isr = new InputStreamReader(socket.getInputStream(),
          StandardCharsets.US_ASCII);
      int read;
      boolean keepReading = true;

      while (keepReading) {
        read = isr.read();
        if (read == -1) {
          System.out.println("---------END OF TRANSMISSION----------");
          keepReading = false;
        } else {
          char c = (char) read;
          System.out.print(c);
        }
      }
    }
  }


  private static String getResponseDetails(HttpResponse<String> response) {
    return "version=" + response.version()
        + System.lineSeparator() + "statusCode=" + response.statusCode()
        + System.lineSeparator() + "headers=" + response.headers()
        + System.lineSeparator() + "body=" + response.body();
  }

  private static String googleGetRequest() {
    return "GET / HTTP/1.1\r\n\r\n"
        + "host: www.google.com\r\n";
  }
}
