package HTTPServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class RequestReader {

  private static final String CONTENT_LENGTH_FIELD = "content-length";
  private static final String TRANSFER_ENCODING_FIELD = "transfer-encoding";

  public static Request readRequest(InputStream is) throws IOException, RequestException {
    RequestLine requestLine = RequestLineReader.readRequestLine(is);
    HashMap<String, String> headers = HeaderReader.readHeaderLines(is);
// todo
    return new Request();
  }



}
