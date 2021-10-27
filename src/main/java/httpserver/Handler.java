package httpserver;

public interface Handler {

  String respond(Request request) throws RequestException;
  void setMapping(String url);
}
