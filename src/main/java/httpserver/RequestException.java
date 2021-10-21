package httpserver;

public class RequestException extends Exception {
  private StatusCode statusCode;

  public RequestException(StatusCode statusCode) {
    this.statusCode = statusCode;
  }

  public RequestException(String message, StatusCode statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public StatusCode getResponseCode() {
    return statusCode;
  }
}
