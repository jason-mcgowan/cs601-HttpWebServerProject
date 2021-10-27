package Project3Demo;

import cs601.project1.FileJsonParser;
import cs601.project1.Review;
import cs601.project1.SearchTableP1;
import httpserver.Server;
import httpserver.handlers.FindHandler;
import httpserver.handlers.ReviewSearchHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;

public class Test {

  public static void main(String[] args)
      throws IOException {
    testServer();

//    SearchTableP1<Review> reviews = new SearchTableP1<>();
//    String file = "Cell_Phones_and_Accessories_5_short.json";
//    FileJsonParser.parseByStream(Paths.get(file), Review.class, reviews::add);
//    System.out.println(reviews.fullWordSearch("venezuela"));
  }


  private static void testServer() throws IOException {
    Server server = new Server();
    SearchTableP1<Review> reviews = new SearchTableP1<>();
    FileJsonParser.parseByStream(Paths.get("Cell_Phones_and_Accessories_5_short.json"),
        Review.class, reviews::add);
    String rshMap = "/reviewsearch";
    String domain = "localhost:8080";
    ReviewSearchHandler<Review> rsh = new ReviewSearchHandler<>(reviews, rshMap, domain);
    server.addMapping(rshMap, rsh);

    String findMap = "/find";
    FindHandler<Review> tafHandler = new FindHandler<>(reviews, findMap, domain);
    server.addMapping(findMap, tafHandler);

    server.start(8080);

    try (Scanner scanner = new Scanner(System.in)) {
      boolean running = true;
      String input;
      while (running) {
        System.out.print("\n" + "Type shutdown> ");
        input = scanner.nextLine();
        if (input.equalsIgnoreCase("shutdown")) {
          System.out.println("Shutting down...");
          server.shutdown();
          System.out.println("Server shutdown");
          running = false;
        }
      }
    }

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
