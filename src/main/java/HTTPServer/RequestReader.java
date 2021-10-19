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
    if (messageHasBody(headers)) {
      body = readBody(isr);
    } else {
      body = "";
    }
    return new Request(requestLine, headers, body);
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
