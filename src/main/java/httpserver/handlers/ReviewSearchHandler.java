package httpserver.handlers;

import cs601.project1.Counter;
import cs601.project1.FileJsonParser;
import cs601.project1.Review;
import cs601.project1.SearchTableP1;
import cs601.project1.SearchableP1;
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
import java.util.stream.Stream;

public class ReviewSearchHandler<T extends SearchableP1> extends SearchTableHandler<T> {

  public ReviewSearchHandler(SearchTableP1<T> table, String mapping, String domain) {
    super(table, mapping, domain);
  }

  @Override
  protected String initPostKey() {
    return "query";
  }

  @Override
  protected String getSearchBoxLabel() {
    return "Review text search word:";
  }

  @Override
  protected Stream<T> searchResults(String term) {
    return table.fullWordSearchStream(term).map(Counter::getObject);
  }
}
