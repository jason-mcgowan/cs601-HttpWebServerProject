package httpserver.handlers;

import cs601.project1.FileJsonParser;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ReviewSearchHandler implements Handler {

  private static final String QUERY_KEY = "query=";

  private final SearchTableP1<Review> reviews = new SearchTableP1<>();
  private final String mapping;
  private final String domain;
  private final String getResponse;

  public ReviewSearchHandler(String mapping, String domain) {
    this.mapping = mapping;
    this.domain = domain;
    getResponse = initializeGetResponse();
  }

  private String initializeGetResponse() {
    String form = HtmlBuilder.inputTextForPost(mapping, "Word to search:", "query",
        "Search");
    String page = HtmlBuilder.simplePage(domain, "Review Search", form);
    return Responses.getMessage(page);
  }

  @Override
  public String respond(Request request) throws RequestException {
    Method method = request.getRequestLine().getMethod();
    switch (method) {
      case GET -> {
        return getResponse;
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
    String body = buildBodyFromSearchResults(term);
    String page = HtmlBuilder.simplePage(domain, "Results", body);
    return Responses.getMessage(page);
  }

  private String buildBodyFromSearchResults(String term) {
    StringBuilder sb = new StringBuilder();
    reviews.fullWordSearchStream(term).forEach(r -> sb.append("<p>").append(r).append("</p>"));
    return sb.toString();
  }

  private String getTermOrThrow(String body) throws RequestException {
    int qIndex = body.indexOf(QUERY_KEY);
    if (qIndex == -1) {
      throw new RequestException("Message body does not include " + QUERY_KEY + "key",
          StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
    int termInd = qIndex + QUERY_KEY.length();
    String decoded = URLDecoder.decode(body.substring(termInd), StandardCharsets.ISO_8859_1);
    return decoded.split(" ", 1)[0];
  }

  public void parseInReviews(Path path) throws IOException {
    FileJsonParser.parseByStream(path, Review.class, reviews::add);
  }
}
