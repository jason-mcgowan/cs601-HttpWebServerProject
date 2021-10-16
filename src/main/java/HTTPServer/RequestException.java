package HTTPServer;

public class RequestException extends Exception {
  private ResponseCode responseCode;

  public RequestException(ResponseCode responseCode) {
    this.responseCode = responseCode;
  }

  public RequestException(String message, ResponseCode responseCode) {
    super(message);
    this.responseCode = responseCode;
  }

  public ResponseCode getResponseCode() {
    return responseCode;
  }
}
