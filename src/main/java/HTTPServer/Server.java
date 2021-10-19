package HTTPServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {

  private final HashMap<String, Handler> handlers = new HashMap<>();
  private final ReentrantReadWriteLock handlerLock = new ReentrantReadWriteLock();
  private final ExecutorService connectionListenerThread = Executors.newSingleThreadExecutor();
  private final ExecutorService clientThreads = Executors.newCachedThreadPool();

  private ServerSocket server;

  public Server() {
  }

  public void addMapping(String term, Handler handler) {
    handlerLock.writeLock().lock();
    handlers.put(term, handler);
    handlerLock.writeLock().unlock();
  }

  public synchronized void start(int port) {
    try {
      server = new ServerSocket(port);
      System.out.println("Server started on port: " + port);
    } catch (IOException e) {
      // todo log
      System.out.println("Error: " + e.getLocalizedMessage());
    }
    connectionListenerThread.execute(this::listenForClients);
  }

  private void listenForClients() {
    try {
      while (!server.isClosed() && server.isBound()) {
        Socket newConnection = server.accept();
        System.out.println("New connection from: " + newConnection);
        clientThreads.execute(() -> handleConnection(newConnection));
      }
    } catch (IOException e) {
      System.out.println("Error connecting");
      // todo log
    }
  }

  private void handleConnection(Socket client) {
    handlerLock.readLock().lock();
    try {
      // todo get and handle request
      // todo send response, close socket
      client.getOutputStream()
          .write(
              ("HTTP/1.1 200 OK\r\n"
                  + "Date: " + Instant.now() + "\r\n"
                  + "Content-type: text/html;charset=us-ascii\r\n"
                  + "Content-length: 2\r\n"
                  + "Connection: close\r\n\r\n"
                  + "Hi\r\n\r\n").getBytes(
                  StandardCharsets.US_ASCII));
      System.out.println("Response sent");
    } catch (IOException e) {
      e.printStackTrace();
//    } catch (RequestException e) {
//      System.out.println("Response code: " + e.getResponseCode());
    } finally {
      handlerLock.readLock().unlock();
    }
  }

}
