package httpserver.util;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public class FileLogger implements EventListener<StringEventArg> {
  private Path path;

  public void logEventHandler(Object obj, StringEventArg msg) {
    System.out.println("Received log: " + msg);
    // todo add file logging
  }

  @Override
  public BiConsumer<Object, StringEventArg> getSubscriber() {
    return this::logEventHandler;
  }
}
