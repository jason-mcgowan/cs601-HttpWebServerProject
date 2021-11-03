package httpserver.util;

import httpserver.RequestException;
import httpserver.protocol.StatusCode;
import httpserver.protocol.Version;
import java.time.Instant;

/**
 * Utility class for providing common HTTP 1.1 responses.
 *
 * @author Jason McGowan
 */
public final class Responses {

  private Responses() {
  }

  /**
   * Returns a canned HTTP response that lists the exception status code and underlying message.
   */
  public static String getStandardErrorResponse(RequestException e) {
    StatusCode code = e.getResponseCode();
    String body = code.getCode() + " " + code.getDescription() + " " + e.getLocalizedMessage();
    return getMessage(Version.HTTP_1_1, code, body);
  }

  /**
   * Returns an HTTP response with Date, Content-type, Content-length, and Connection: close headers
   * in addition to the provided arguments.
   */
  public static String getMessage(Version version, StatusCode code, String body) {
    String vBlock = "HTTP/" + version.getMajorVer() + "." + version.getMinorVer();
    String status = code.getCode() + " " + code.getDescription();
    String date = Instant.now().toString();
    return vBlock + " " + status + "\r\n"
        + "Date: " + date + "\r\n"
        + "Content-type: text/html; charset=US-ASCII\r\n"
        + "Content-length: " + body.length() + "\r\n"
        + "Connection: close\r\n"
        + "\r\n"
        + body;
  }

  /**
   * Returns an HTTP response with status code 200 OK and version HTTP 1.1
   */
  public static String getMessage(String body) {
    return getMessage(Version.HTTP_1_1, StatusCode.SUCCESS_200_OK, body);
  }
}
