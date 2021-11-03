package demo;

import cs601.project1.FileJsonParser;
import cs601.project1.MainArgsHelper;
import cs601.project1.Review;
import cs601.project1.SearchTableP1;
import httpserver.Server;
import httpserver.handlers.ChatHandler;
import httpserver.handlers.FindHandler;
import httpserver.handlers.ReviewSearchHandler;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * The entry point for the project3 demonstration. This class does not have robust argument
 * validation as it is merely a demo of the underlying framework.
 *
 * @author Jason McGowan
 */
public class Driver {

  private static final String REVIEW_FILE_FLAG = "-reviewfile";
  private static final String REVIEW_PORT_FLAG = "-reviewport";
  private static final String SLACKBOT_PORT_FLAG = "-slackport";
  private static final String SLACKBOT_WEBHOOK = "-slackhook";
  private static final String SHUTDOWN_FLAG = "-shutdownkey";
  private static final String DOMAIN_FLAG = "-domain";

  public static void main(String[] args) {
    String reviewFile = MainArgsHelper.getFlagContext(REVIEW_FILE_FLAG, args);
    String reviewPort = MainArgsHelper.getFlagContext(REVIEW_PORT_FLAG, args);
    String slackbotPort = MainArgsHelper.getFlagContext(SLACKBOT_PORT_FLAG, args);
    String slackbotWebhook = MainArgsHelper.getFlagContext(SLACKBOT_WEBHOOK, args);
    String shutdownKey = MainArgsHelper.getFlagContext(SHUTDOWN_FLAG, args);
    String domain = MainArgsHelper.getFlagContext(DOMAIN_FLAG, args);

    if (reviewFile == null || reviewPort == null || slackbotPort == null || slackbotWebhook == null
        || shutdownKey == null || domain == null) {
      System.out.println("Not all arguments provided correctly, exiting");
      return;
    }

    try {
      initializeReviewServer(reviewFile, reviewPort, shutdownKey, domain);
      initializeChatBotServer(slackbotPort, slackbotWebhook, shutdownKey, domain);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static void initializeChatBotServer(String slackbotPort, String slackbotWebhook,
      String shutdownKey,
      String domain) throws IOException {
    Server chatServer = new Server(domain);

    ChatHandler ch = new ChatHandler(slackbotWebhook);
    chatServer.addMapping("/slackbot", ch);

    SelfShutdownLogServer chatSSLS = new SelfShutdownLogServer(chatServer, Paths.get("chatlog.txt"),
        shutdownKey);
    chatSSLS.runUntilRemoveShutdown(Integer.parseInt(slackbotPort));
  }

  private static void initializeReviewServer(String reviewFile, String reviewPort,
      String shutdownKey,
      String domain) throws IOException {
    Server reviewServer = new Server(domain);

    SearchTableP1<Review> reviews = new SearchTableP1<>();
    FileJsonParser.parseByStream(Paths.get(reviewFile), Review.class, reviews::add);

    ReviewSearchHandler<Review> rsh = new ReviewSearchHandler<>(reviews);
    reviewServer.addMapping("/reviewsearch", rsh);

    FindHandler<Review> tafHandler = new FindHandler<>(reviews);
    reviewServer.addMapping("/find", tafHandler);

    SelfShutdownLogServer reviewSelfSdLog = new SelfShutdownLogServer(reviewServer,
        Paths.get("reviewlog.txt"),
        shutdownKey);
    reviewSelfSdLog.runUntilRemoveShutdown(Integer.parseInt(reviewPort));
  }
}
