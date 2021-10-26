package httpserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class HeaderReader {

  private static final char CR = '\r';
  private static final char LF = '\n';
  private static final int LENGTH_LIMIT = 16384;

  private final HashMap<String, String> headers = new HashMap<>();
  private State state = new InFieldFirstChar();
  private StringBuilder current = new StringBuilder();
  private int length = 0;
  private String currentField;

  private HeaderReader() {
  }

  public static HashMap<String, String> readHeaderLines(InputStreamReader isr)
      throws IOException, RequestException {
    HeaderReader hb = new HeaderReader();
    boolean moreToRead = true;
    int read;
    while (moreToRead) {
      read = isr.read();
      if (read == -1) {
        throw new IOException();
      }
      if (++hb.length > LENGTH_LIMIT) {
        throw new RequestException("Header section exceeds max size: " + LENGTH_LIMIT,
            StatusCode.CLIENT_ERROR_413_PAYLOAD_TOO_LARGE);
      }
      moreToRead = hb.addChar((char) read);
    }
    return hb.headers;
  }

  private boolean addChar(char c) throws RequestException {
    return state.addChar(c, this);
  }

  private void addLineToHeaders() {
    headers.merge(currentField.toLowerCase(), current.toString(), (o, n) -> o + "," + n);
    currentField = "";
    current = new StringBuilder();
  }

  // region State Inner Classes
  private interface State {

    boolean addChar(char c, HeaderReader hb) throws RequestException;
  }

  private static final class InFieldFirstChar implements State {

    @Override
    public boolean addChar(char c, HeaderReader hb) throws RequestException {
      if (c == ':' || c == ' ') {
        throw new RequestException("First character of header line invalid",
            StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
      if (c == LF) {
        return true;
      }
      // CR on a new line indicates the start of the end of section termination expression CRLF
      if (c == CR) {
        hb.state = new InSectionTerm();
        return true;
      }
      hb.current.append(c);
      hb.state = new InField();
      return true;
    }
  }

  private static final class InField implements State {

    @Override
    public boolean addChar(char c, HeaderReader hb) throws RequestException {
      if (c == ':') {
        hb.currentField = hb.current.toString();
        hb.current = new StringBuilder();
        hb.state = new InValue();
        return true;
      }
      if (hb.current.isEmpty() && c == CR) {
        hb.state = new InLineTerm();
        return true;
      }
      if (c == ' ' || c == CR || c == LF) {
        throw new RequestException("Header line key contains invalid character",
            StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
      hb.current.append(c);
      return true;
    }
  }

  private static final class InValue implements State {

    @Override
    public boolean addChar(char c, HeaderReader hb) throws RequestException {
      // Ignore optional leading white space
      if (hb.current.isEmpty() && c == ' ') {
        return true;
      }
      if (c == LF) {
        throw new RequestException("Invalid line feed in value for key: " + hb.currentField,
            StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
      // CR indicates the first term of the end of line expression
      if (c == CR) {
        if (hb.current.isEmpty()) {  // Problem if you're ending the line without any terms
          throw new RequestException("Value is empty for key: " + hb.currentField,
              StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
        hb.addLineToHeaders();
        hb.state = new InLineTerm();
        return true;
      }
      hb.current.append(c);
      return true;
    }
  }

  private static final class InLineTerm implements State {

    @Override
    public boolean addChar(char c, HeaderReader hb) throws RequestException {
      // Anything other than LF breaks the line end expression
      if (c != LF) {
        throw new RequestException("Header line terminates incorrectly",
            StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
      hb.state = new InFieldFirstChar();
      return true;
    }
  }

  private static final class InSectionTerm implements State {

    @Override
    public boolean addChar(char c, HeaderReader hb) throws RequestException {
      if (c != LF) {
        throw new RequestException("Header section terminates incorrectly",
            StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
      return false;
    }
  }
  // endregion
}
