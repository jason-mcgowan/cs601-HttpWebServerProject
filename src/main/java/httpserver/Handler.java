package httpserver;

import java.io.IOException;

/**
 * Interface providing method signatures for server access.
 *
 * @author Jason McGowan
 */
public interface Handler {

  /**
   * When provided a Request object, returns the valid HTTP response.
   */
  String respond(Request request) throws RequestException, IOException;

  void setDomain(String domain);

  void setUrl(String url);
}
