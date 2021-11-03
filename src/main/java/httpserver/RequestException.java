package httpserver;

import httpserver.protocol.StatusCode;

/**
 * Contains the status code associated with the HTTP violation.
 *
 * @author Jason McGowan
 */
public class RequestException extends Exception {
  private final StatusCode statusCode;

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
