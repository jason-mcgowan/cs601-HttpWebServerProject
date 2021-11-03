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

/**
 * Listens for string events to log to the provided file prepended with the current system UTC time.
 * NOTE: These must be closed in order to close its BufferedWriter.
 * <p>
 * Writing is done with a separate thread supported by a blocking queue.
 *
 * @author Jason McGowan
 */
public class FileLogger implements EventListener<String>, AutoCloseable, Closeable {

  private final BufferedWriter bw;
  private final ExecutorService thread = Executors.newSingleThreadExecutor();

  /**
   * Instantiates a BufferedWriter which must be safely closed.
   */
  public FileLogger(Path path) throws IOException {
    bw = Files.newBufferedWriter(path);
  }

  public void logEventHandler(Object obj, String msg) {
    String text = Instant.now() + ": " + msg + System.lineSeparator();
    thread.execute(() -> writeToFile(text));
  }

  /**
   * Shuts down the thread writer with a 5 second delay for forced termination. Flushes and closes
   * the BufferedWriter.
   */
  @Override
  public synchronized void close() throws IOException {
    try {
      thread.shutdown();
      if (!thread.awaitTermination(5, TimeUnit.SECONDS)) {
        thread.shutdownNow();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      bw.flush();
      bw.close();
    }
  }

  @Override
  public BiConsumer<Object, String> getSubscriber() {
    return this::logEventHandler;
  }

  private void writeToFile(String text) {
    try {
      bw.write(text);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
