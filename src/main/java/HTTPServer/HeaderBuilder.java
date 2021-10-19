package HTTPServer;

import java.io.IOException;
import java.util.HashMap;

public class HeaderBuilder {

  private static final String LINE_TERM = "\r\n";

  private final HashMap<String, String> headers = new HashMap<>();
  private State state = new InField();
  private StringBuilder current = new StringBuilder();
  private String currentField;

  /**
   * Adds in a character for parsing an HTTP header section. Returns true when more characters are
   * expected. Strips optional white space between field and value.
   * @return True when more characters are expected.
   * @throws IOException If the character violates protocol (space in field or invalid termination)
   */
  public boolean continueReading(char c) throws IOException {
    return state.addChar(c, this);
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }

  private interface State {
    boolean addChar(char c, HeaderBuilder hb) throws IOException;
  }

  private static final class InField implements State {
    @Override
    public boolean addChar(char c, HeaderBuilder hb) throws IOException {
      if (c == ':') {
        hb.currentField = hb.current.toString();
        hb.current = new StringBuilder();
        hb.state = new InValue();
        return true;
      }
      if (c == ' ') {
        throw new IOException();
      }
      if (hb.current.isEmpty() && c == LINE_TERM.charAt(0)){
        hb.state = new InLineTerm();
        return true;
      }
      hb.current.append(c);
      return true;
    }
  }

  private static final class InValue implements State {
    @Override
    public boolean addChar(char c, HeaderBuilder hb) throws IOException {
      // Ignore optional leading white space
      if (hb.current.isEmpty() && c == ' '){
        return true;
      }
      if (c == ':') {
        throw new IOException();
      }
      // When you reach the end of the value terms, add it with the field to the header map
      if (c == LINE_TERM.charAt(0)){
        hb.headers.put(hb.currentField, hb.current.toString());
        hb.current = new StringBuilder();
        hb.state = new InLineTerm();
        return true;
      }
      hb.current.append(c);
      return true;
    }
  }

  private static final class InLineTerm implements State {

    @Override
    public boolean addChar(char c, HeaderBuilder hb) throws IOException {
      if (c != LINE_TERM.charAt(1)) {
        throw new IOException();
      }
      return !hb.current.isEmpty();
    }
  }

}
