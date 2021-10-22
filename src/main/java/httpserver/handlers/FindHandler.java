package httpserver.handlers;

import cs601.project1.SearchTableP1;
import cs601.project1.SearchableP1;
import java.util.stream.Stream;

public class FindHandler<T extends SearchableP1> extends SearchTableHandler<T> {

  public FindHandler(SearchTableP1<T> table, String mapping,
      String domain) {
    super(table, mapping, domain);
  }

  @Override
  protected String initPostKey() {
    return "find";
  }

  @Override
  protected String getSearchBoxLabel() {
    return "ASIN to search:";
  }

  @Override
  protected Stream<T> searchResults(String term) {
    return table.findAsinStream(term);
  }
}
