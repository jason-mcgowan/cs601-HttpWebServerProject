package httpserver;

import httpserver.protocol.Method;
import httpserver.protocol.StatusCode;
import httpserver.protocol.Version;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility class for reading in the request line for an HTTP 1.1 request message from the provided
 * input stream.
 *
 * @author Jason McGowan
 */
public final class RequestLineReader {

  private static final int SPACE_ASCII_VAL = 32;
  private static final int MAX_URI_LENGTH = 2048;
  private static final String REQUEST_LINE_FINISH = "\r\n";
  private static final int VERSION_LENGTH = 8;

  private RequestLineReader() {
  }

  /**
   * @param isr InputStreamReader at the first byte of an incoming HTTP request message.
   * @return A valid RequestLine object
   * @throws RequestException Contains status code associated with violating framework rules or
   *                          protocol
   */
  public static RequestLine readRequestLine(InputStreamReader isr)
      throws IOException, RequestException {
    Method method = readMethod(isr);
    String uri = readURI(isr);
    Version version = readVersion(isr);
    readLineEnd(isr);
    return new RequestLine(method, uri, version);
  }

  private static Method readMethod(InputStreamReader isr) throws IOException, RequestException {
    StringBuilder sb = new StringBuilder();
    int read = isr.read();

    while (read != SPACE_ASCII_VAL) { // Space indicates the end of the method name
      throwIfInvalidRead(read);
      if (sb.length() >= Method.MAX_LENGTH) { // Prevent malicious long gibberish requests
        throw new RequestException(StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
      sb.append((char) read);
      read = isr.read();
    }
    Method method = Method.getExact(sb.toString());
    if (method == null) {
      throw new RequestException(StatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
    }
    return method;
  }

  private static String readURI(InputStreamReader isr) throws IOException, RequestException {
    StringBuilder sb = new StringBuilder();
    int read = isr.read();
    while (read != SPACE_ASCII_VAL) {
      throwIfInvalidRead(read);
      if (sb.length() >= MAX_URI_LENGTH) {
        throw new RequestException(StatusCode.CLIENT_ERROR_414_URI_TOO_LONG);
      }
      sb.append((char) read);
      read = isr.read();
    }
    return sb.toString();
  }

  private static Version readVersion(InputStreamReader isr) throws IOException, RequestException {
    StringBuilder sb = new StringBuilder();
    int read;

    for (int i = 0; i < VERSION_LENGTH; i++) {
      read = isr.read();
      throwIfInvalidRead(read);
      sb.append((char) read);
    }
    throwIfVersionFormatIncorrect(sb.toString());
    int majorVer = Integer.parseInt(sb.substring(5, 6));
    int minorVer = Integer.parseInt(sb.substring(7, 8));
    Version version = Version.getExact(majorVer, minorVer);
    if (version == null) {
      throw new RequestException(StatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
    }
    return version;
  }

  private static void throwIfVersionFormatIncorrect(String versionText) throws RequestException {
    if (!versionText.matches("HTTP/\\d\\.\\d")) {
      throw new RequestException(StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
  }

  private static void throwIfInvalidRead(int read) throws RequestException {
    if (read == -1 || read == '\r' || read == '\n') {
      throw new RequestException(StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
  }

  private static void readLineEnd(InputStreamReader isr) throws RequestException, IOException {
    int read;

    for (int i = 0; i < REQUEST_LINE_FINISH.length(); i++) {
      read = isr.read();
      if (read == -1) {
        throw new RequestException(StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
      if ((char) read != REQUEST_LINE_FINISH.charAt(i)) {
        throw new RequestException(StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
    }
  }
}
