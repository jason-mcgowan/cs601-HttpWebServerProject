package httpserver.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cs601.project1.Review;
import cs601.project1.SearchTableP1;
import httpserver.Handler;
import httpserver.Method;
import httpserver.Request;
import httpserver.RequestException;
import httpserver.Responses;
import httpserver.StatusCode;
import httpserver.util.HtmlBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ReviewSearchHandler implements Handler {

  private static final String GET_RESPONSE = initializeGetResponse();
  private static final String QUERY_KEY = "query=";

  private SearchTableP1<Review> reviews;

  public ReviewSearchHandler() {
  }

  private static String initializeGetResponse() {
    String form = HtmlBuilder.inputTextForPost("/reviewsearch", "Word to search:", "query",
        "Search");
    String page = HtmlBuilder.simplePage("localhost:8080", "Review Search", form);
    return Responses.getMessage(page);
  }

  @Override
  public String respond(Request request) throws RequestException {
    Method method = request.getRequestLine().getMethod();
    switch (method) {
      case GET -> {
        return GET_RESPONSE;
      }
      case POST -> {
        return postResponse(request);
      }
      default -> throw new RequestException("Method not supported: " + method.getId(),
          StatusCode.CLIENT_ERROR_405_METHOD_NOT_ALLOWED);
    }
  }

  private String postResponse(Request request) throws RequestException {
    String term = getTermOrThrow(request.getBody());
    String body = reviews.fullWordSearch(term);
    return Responses.getMessage(body);
  }

  private String getTermOrThrow(String body) throws RequestException {
    int qIndex = body.indexOf(QUERY_KEY);
    if (qIndex == -1) {
      throw new RequestException("Message body does not include " + QUERY_KEY + "term",
          StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
    int termInd = qIndex + QUERY_KEY.length();
    // todo add in URL decoder
    return body.substring(termInd).split(" ", 1)[0];
  }

  public void parseInReviews(Path path) throws IOException {
    Gson gson = new GsonBuilder().setLenient().create();

    try (Stream<String> lines = Files.lines(path, StandardCharsets.ISO_8859_1)) {
      lines
          .map(line -> parse(gson, line))
          .forEach(
              review -> {
                if (review != null) {
                  reviews.add(review);
                }
              });
    }
  }

  private Review parse(Gson gson, String json) {
    try {
      return gson.fromJson(json, Review.class);
    } catch (JsonSyntaxException e) {
      // todo logging
      return null;
    }
  }
}
