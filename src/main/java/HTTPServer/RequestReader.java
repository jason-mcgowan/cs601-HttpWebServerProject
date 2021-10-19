package HTTPServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class RequestReader {

  private static final String CONTENT_LENGTH_FIELD = "content-length";
  private static final String TRANSFER_ENCODING_FIELD = "transfer-encoding";

  public static Request readRequest(InputStreamReader isr) throws IOException, RequestException {
    RequestLine requestLine = RequestLineReader.readRequestLine(isr);
    HashMap<String, String> headers = HeaderReader.readHeaderLines(isr);
    String body;
    if (headers.containsKey(CONTENT_LENGTH_FIELD)) {
      try {
        int bodySize = Integer.parseInt(headers.get(CONTENT_LENGTH_FIELD));
        body = readToString(isr, bodySize);
      } catch (NumberFormatException e) {
        throw new RequestException("content-length value not an integer",
            ResponseCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
    } else if (headers.containsKey(TRANSFER_ENCODING_FIELD)) {
      throw new RequestException(
          "Server only supports payload bodies using content-length header",
          ResponseCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
    } else {
      body = null;
    }
    return new Request(requestLine, headers, body);
  }

  private static String readToString(InputStreamReader isr, int length) throws IOException {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(isr.read());
    }
    return sb.toString();
  }

  private static String readBody(InputStreamReader isr) {
    // todo
    return null;
  }

  private static boolean messageHasBody(HashMap<String, String> headers) {
    return headers.containsKey(CONTENT_LENGTH_FIELD) || headers.containsKey(
        TRANSFER_ENCODING_FIELD);
  }
}
