package httpserver.protocol;

// https://www.rfc-editor.org/rfc/rfc7231.html#section-6
public enum StatusCode {
  SUCCESS_200_OK("200", "OK"),
  CLIENT_ERROR_400_BAD_REQUEST("400", "Bad Request"),
  CLIENT_ERROR_404_NOT_FOUND("404", "Not Found"),
  CLIENT_ERROR_405_METHOD_NOT_ALLOWED("405", "Method Not Allowed"),
  CLIENT_ERROR_413_PAYLOAD_TOO_LARGE("413", "Payload Too Large"),
  CLIENT_ERROR_414_URI_TOO_LONG("414", "URI Too Long"),
  SERVER_ERROR_500_INTERNAL_SERVER_ERROR("500", "Internal Server Error"),
  SERVER_ERROR_501_NOT_IMPLEMENTED("501", "Not Implemented"),
  SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED("505", "HTTP Version Not Supported");

  private final String code;
  private final String description;

  StatusCode(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }
}
