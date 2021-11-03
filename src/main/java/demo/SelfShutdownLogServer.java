package demo;

import httpserver.Server;
import httpserver.handlers.ShutdownHandler;
import httpserver.util.FileLogger;
import java.io.IOException;
import java.nio.file.Path;

public class SelfShutdownLogServer {

  private Server server;
  private FileLogger logger;
  private ShutdownHandler shutdownHandler;

  private SelfShutdownLogServer() {
  }

  public SelfShutdownLogServer (Server server, Path loggerFile, String shutdownKey)
      throws IOException {
    this.server = server;
    this.logger = new FileLogger(loggerFile);
    initializeLogging();
    this.shutdownHandler = new ShutdownHandler(shutdownKey);
    initializeShutdownListener();
  }

  public void runUntilRemoveShutdown(int port) throws IOException {
    server.start(port);
  }

  private void initializeShutdownListener() {
    shutdownHandler.getShutdownRequested().subscribe(this::shutdown);
    server.addMapping("/shutdown", shutdownHandler);
  }

  private void shutdown(Object obj, Object arg) {
    new Thread(()->{
      try {
        server.shutdown();
        logger.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  private void initializeLogging() {
    server.getLogEvent().subscribe(logger.getSubscriber());
  }
}