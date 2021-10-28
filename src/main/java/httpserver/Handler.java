package httpserver;

import java.io.IOException;

public interface Handler {

  String respond(Request request) throws RequestException, IOException, InterruptedException;
  void setMapping(String url);
}
