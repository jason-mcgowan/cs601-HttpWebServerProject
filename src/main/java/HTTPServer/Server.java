package HTTPServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

  private synchronized void handleConnection(Socket client) {
    handlerLock.readLock().lock();
    try (InputStreamReader isr = new InputStreamReader(client.getInputStream(),
        StandardCharsets.US_ASCII)) {
      Request request = RequestReader.readRequest(isr);
      // todo send response, close socket
    } catch (IOException | RequestException e) {
      // todo send response to client
    } finally {
      handlerLock.readLock().unlock();
    }
  }
}
