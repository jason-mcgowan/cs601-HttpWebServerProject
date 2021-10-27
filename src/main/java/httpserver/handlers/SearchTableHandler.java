package httpserver.handlers;

import cs601.project1.SearchTableP1;
import cs601.project1.SearchableP1;
import httpserver.Handler;
import httpserver.Method;
import httpserver.Request;
import httpserver.RequestException;
import httpserver.Responses;
import httpserver.StatusCode;
import httpserver.util.HtmlBuilder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public abstract class SearchTableHandler<T extends SearchableP1> implements Handler {

  protected final SearchTableP1<T> table;
  private final String postKey;
  private final String domain;
  private String getResponse;

  protected SearchTableHandler(SearchTableP1<T> table, String domain) {
    this.table = table;
    this.domain = domain;
    this.postKey = initPostKey();
  }

  protected abstract String initPostKey();

  protected abstract String getSearchBoxLabel();

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

  @Override
  public void setMapping(String url) {
    getResponse = buildGetResponse(url);
  }

  private String buildGetResponse(String url) {
    String form = HtmlBuilder.inputTextForPost(url, getSearchBoxLabel(), postKey,
        "Search");
    String page = HtmlBuilder.simplePage(domain, "Record Search", form);
    return Responses.getMessage(page);
  }

  private String postResponse(Request request) throws RequestException {
    String term = getTermOrThrow(request.getBody());
    String body = buildBodyFromSearchResults(term);
    String page = HtmlBuilder.simplePage(domain, "Results", body);
    return Responses.getMessage(page);
  }

  private String buildBodyFromSearchResults(String term) {
    StringBuilder sb = new StringBuilder();
    searchResults(term).forEach(r -> sb.append("<p>").append(r).append("</p>"));
    return sb.toString();
  }

  protected abstract Stream<T> searchResults(String term);

  private String getTermOrThrow(String body) throws RequestException {
    String decoded = URLDecoder.decode(body, StandardCharsets.US_ASCII);
    String keyId = postKey + "=";
    if (!decoded.startsWith(keyId)){
      throw new RequestException("Message body does not start with key: " + postKey,
          StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
    int termInd = keyId.length();
    return decoded.substring(termInd).split(" ", 1)[0];
  }
}
