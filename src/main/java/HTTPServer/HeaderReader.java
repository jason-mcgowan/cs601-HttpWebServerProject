package HTTPServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HeaderReader {

  private static final char CR = '\r';
  private static final char LF = '\n';

  private final HashMap<String, String> headers = new HashMap<>();
  private State state = new InFieldFirstChar();
  private StringBuilder current = new StringBuilder();
  private String currentField;

  public static HashMap<String, String> readHeaderLines(InputStream is) throws IOException {
    HeaderReader hb = new HeaderReader();
    boolean keepReading = true;
    int read;
    try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.US_ASCII)){
      while (keepReading) {
        read = isr.read();
        if (read == -1) {
          throw new IOException();
        }
        keepReading = hb.continueReading((char) read);
      }
    }
    return hb.headers;
  }

  /**
   * Adds in a character for parsing an HTTP header section. Returns true when more characters are
   * expected. Strips optional white space between field and value.
   *
   * @return True when more characters are expected.
   * @throws IOException If the character violates protocol (space in field or invalid termination)
   */
  private boolean continueReading(char c) throws IOException {
    return state.addChar(c, this);
  }

  private interface State {

    boolean addChar(char c, HeaderReader hb) throws IOException;
  }

  private static final class InFieldFirstChar implements State {

    @Override
    public boolean addChar(char c, HeaderReader hb) throws IOException {
      if (c == ':' || c == ' ') {
        throw new IOException();
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
    public boolean addChar(char c, HeaderReader hb) throws IOException {
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
        throw new IOException();
      }
      hb.current.append(c);
      return true;
    }
  }

  private static final class InValue implements State {

    @Override
    public boolean addChar(char c, HeaderReader hb) throws IOException {
      // Ignore optional leading white space
      if (hb.current.isEmpty() && c == ' ') {
        return true;
      }
      if (c == ':' || c == LF) {
        throw new IOException();
      }
      // CR indicates the first term of the end of line expression
      if (c == CR) {
        if (hb.current.isEmpty()) {  // Problem if you're ending the line without any terms
          throw new IOException();
        }
        hb.headers.put(hb.currentField, hb.current.toString());
        hb.currentField = "";
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
    public boolean addChar(char c, HeaderReader hb) throws IOException {
      // Anything other than LF breaks the line end expression
      if (c != LF) {
        throw new IOException();
      }
      hb.state = new InFieldFirstChar();
      return true;
    }
  }

  private static final class InSectionTerm implements State {

    @Override
    public boolean addChar(char c, HeaderReader hb) throws IOException {
      if (c != LF) {
        throw new IOException();
      }
      return false;
    }
  }
}
