package httpserver;

import java.io.IOException;

public interface Handler {

  String respond(Request request) throws RequestException, IOException;
  void setMapping(String url);
}
