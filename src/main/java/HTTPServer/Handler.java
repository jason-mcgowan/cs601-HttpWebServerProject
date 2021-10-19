package HTTPServer;

import java.net.Socket;

public interface Handler {
  boolean handle(Socket connection, Request request);

  // todo
}
