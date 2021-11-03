package httpserver;

import java.io.IOException;

public interface Handler {

  String respond(Request request) throws RequestException, IOException;
  void setDomain(String domain);
  void setUrl(String url);
}
