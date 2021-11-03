package httpserver.handlers;

import cs601.project1.Counter;
import cs601.project1.SearchTableP1;
import cs601.project1.SearchableP1;
import java.util.stream.Stream;

/**
 * Returns all reviews containing at least one occurrence of the provided single word.
 *
 * @author Jason McGowan
 */
public class ReviewSearchHandler<T extends SearchableP1> extends SearchTableHandler<T> {

  public ReviewSearchHandler(SearchTableP1<T> table) {
    super(table);
  }

  @Override
  protected String initPostKey() {
    return "query";
  }

  @Override
  protected String getInputTextBoxLabel() {
    return "Single word to search for";
  }

  @Override
  protected String getGetPageTitle() {
    return "Review Search";
  }

  @Override
  protected Stream<T> searchResults(String term) {
    return table.fullWordSearchStream(term).map(Counter::getObject);
  }
}
