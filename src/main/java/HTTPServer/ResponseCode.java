package HTTPServer;

// https://www.rfc-editor.org/rfc/rfc7231.html#section-6
public enum ResponseCode {
  SUCCESS_200_OK("OK"),
  CLIENT_ERROR_400_BAD_REQUEST("Bad Request"),
  CLIENT_ERROR_405_METHOD_NOT_ALLOWED("Method Not Allowed"),
  CLIENT_ERROR_414_URI_TOO_LONG("URI Too Long"),
  SERVER_ERROR_501_NOT_IMPLEMENTED("Not Implemented"),
  SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED("HTTP Version Not Supported")
  ;

  private final String name;

  ResponseCode(String name) {
    this.name = name;
  }
}
