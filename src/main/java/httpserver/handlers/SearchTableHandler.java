package httpserver.handlers;

import cs601.project1.SearchTableP1;
import cs601.project1.SearchableP1;
import httpserver.util.Responses;
import httpserver.util.HtmlBuilder;
import java.io.IOException;
import java.util.stream.Stream;

public abstract class SearchTableHandler<T extends SearchableP1> extends SingleInputHandler {

  protected final SearchTableP1<T> table;

  public SearchTableHandler(String domain, SearchTableP1<T> table) {
    super(domain);
    this.table = table;
  }

  @Override
  protected String getPostTermResponse(String term) throws IOException {
    String body = buildBodyFromSearchResults(term);
    String page = HtmlBuilder.simplePage(domain, "Search Results", body);
    return Responses.getMessage(page);
  }

  private String buildBodyFromSearchResults(String term) {
    StringBuilder sb = new StringBuilder();
    searchResults(term).forEach(r -> sb.append("<p>").append(r).append("</p>"));
    return sb.toString();
  }

  protected abstract Stream<T> searchResults(String term);
}
