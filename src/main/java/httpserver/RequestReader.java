package httpserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class RequestReader {

  private static final String CONTENT_LENGTH_FIELD = "content-length";
  private static final String TRANSFER_ENCODING_FIELD = "transfer-encoding";

  public static Request readRequest(InputStreamReader isr) throws IOException, RequestException {
    RequestLine requestLine = RequestLineReader.readRequestLine(isr);
    HashMap<String, String> headers = HeaderReader.readHeaderLines(isr);
    String body = null;
    if (headers.containsKey(CONTENT_LENGTH_FIELD)) {
      body = getMessageBodyFromContentLength(isr, headers);
    } else if (headers.containsKey(TRANSFER_ENCODING_FIELD)) {
      throw new RequestException(
          "Server only supports payload bodies using content-length header",
          StatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
    }
    return new Request(requestLine, headers, body);
  }

  private static String getMessageBodyFromContentLength(InputStreamReader isr,
      HashMap<String, String> headers)
      throws IOException, RequestException {
    String body;
    try {
      int bodySize = Integer.parseInt(headers.get(CONTENT_LENGTH_FIELD));
      body = readToString(isr, bodySize);
    } catch (NumberFormatException e) {
      throw new RequestException("content-length value not an integer",
          StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
    return body;
  }

  private static String readToString(InputStreamReader isr, int length) throws IOException {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append((char) isr.read());
    }
    return sb.toString();
  }

  private static boolean messageHasBody(HashMap<String, String> headers) {
    return headers.containsKey(CONTENT_LENGTH_FIELD) || headers.containsKey(
        TRANSFER_ENCODING_FIELD);
  }
}
