package httpserver.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class FileLogger implements EventListener<StringEventArg>, AutoCloseable, Closeable {

  private final BufferedWriter bw;
  private final ExecutorService thread = Executors.newSingleThreadExecutor();

  public FileLogger(Path path) throws IOException {
    bw = Files.newBufferedWriter(path);
  }

  public void logEventHandler(Object obj, StringEventArg msg) {
    String text = Instant.now() + ": " + msg.toString() + System.lineSeparator();
    thread.execute(() -> writeToFile(text));
  }

  private void writeToFile(String text) {
    try {
      bw.write(text);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public BiConsumer<Object, StringEventArg> getSubscriber() {
    return this::logEventHandler;
  }

  @Override
  public synchronized void close() throws IOException {
    bw.flush();
    bw.close();
    thread.shutdown();
    try {
      if (!thread.awaitTermination(5, TimeUnit.SECONDS)) {
        thread.shutdownNow();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
