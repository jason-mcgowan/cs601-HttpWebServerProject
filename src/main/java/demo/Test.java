package demo;

import cs601.project1.FileJsonParser;
import cs601.project1.Review;
import cs601.project1.SearchTableP1;
import httpserver.Server;
import httpserver.handlers.ChatHandler;
import httpserver.handlers.FindHandler;
import httpserver.handlers.ReviewSearchHandler;
import httpserver.handlers.ShutdownHandler;
import httpserver.util.FileLogger;
import java.io.IOException;
import java.nio.file.Paths;

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
    FileLogger logger = new FileLogger(Paths.get("log.txt"));
    server.getLogEvent().subscribe(logger.getSubscriber());

    SearchTableP1<Review> reviews = new SearchTableP1<>();
    FileJsonParser.parseByStream(Paths.get("Cell_Phones_and_Accessories_5_short.json"),
        Review.class, reviews::add);
    String rshMap = "/reviewsearch";
    String domain = "localhost:8080";
    ReviewSearchHandler<Review> rsh = new ReviewSearchHandler<>(domain, reviews);
    server.addMapping(rshMap, rsh);

    String findMap = "/find";
    FindHandler<Review> tafHandler = new FindHandler<>(domain, reviews);
    server.addMapping(findMap, tafHandler);

    ChatHandler ch = new ChatHandler(domain,
        "https://hooks.slack.com/services/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");
    server.addMapping("/slackbot", ch);

    ShutdownHandler shutdownHandler = new ShutdownHandler(domain, "test123");
    server.addMapping("/shutdown", shutdownHandler);
    shutdownHandler.getShutdownRequested().subscribe((o, a) -> System.out.println("Shutdown requested"));

    server.start(8080);

    server.getShutdownEvent().subscribe((o, a) -> {
      try {
        logger.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
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

//  private static String getResponseDetails(HttpResponse<String> response) {
//    return "version=" + response.version()
//        + System.lineSeparator() + "statusCode=" + response.statusCode()
//        + System.lineSeparator() + "headers=" + response.headers()
//        + System.lineSeparator() + "body=" + response.body();
//  }
}
