package httpserver;

import java.time.Instant;

public class Responses {


  public static String getTestPage() {
    String body = "<html>" +
        "<head><title>TEST</title></head>" +
        "<body>This is a short test page.</body>" +
        "</html>";
    return getMessage(Version.HTTP_1_1, StatusCode.SUCCESS_200_OK, body);
  }

  public static String getStandardErrorResponse(RequestException e) {
    StatusCode code = e.getResponseCode();
    String body = code.getCode() + " " + code.getDescription() + " " + e.getLocalizedMessage();
    return getMessage(Version.HTTP_1_1, code, body);
  }

  public static String getMessage(Version version, StatusCode code, String body) {
    String vBlock = "HTTP/" + version.getMajorVer() + "." + version.getMinorVer();
    String status = code.getCode() + " " + code.getDescription();
    String date = Instant.now().toString();
    return vBlock + " " + status + "\r\n"
        + "Date: " + date + "\r\n"
        + "Content-type: text/html; charset=US-ASCII\r\n"
        + "Content-length: " + body.length() + "\r\n"
        + "Connection: close\r\n"
//        + "Connection: close\r\n"
        + "\r\n"
        + body;
  }

  public static String getMessage(String body) {
    return getMessage(Version.HTTP_1_1, StatusCode.SUCCESS_200_OK, body);
  }
}
