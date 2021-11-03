package httpserver.handlers;

import cs601.project1.SearchTableP1;
import cs601.project1.SearchableP1;
import httpserver.util.HtmlBuilder;
import httpserver.util.Responses;
import java.util.stream.Stream;

/**
 * An abstract class providing common functionality for SearchTableP1 queries.
 *
 * @author Jason McGowan
 */
public abstract class SearchTableHandler<T extends SearchableP1> extends SingleInputHandler {

  protected final SearchTableP1<T> table;

  public SearchTableHandler(SearchTableP1<T> table) {
    this.table = table;
  }

  @Override
  protected final String getPostTermResponse(String term) {
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
