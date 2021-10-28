package httpserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

  public void addMapping(String url, Handler handler) {
    handlerLock.writeLock().lock();
    try {
      handlers.put(url, handler);
    } finally {
      handlerLock.writeLock().unlock();
    }
    handler.setMapping(url);
  }

  public synchronized void start(int port) throws IOException {
    server = new ServerSocket(port);
    System.out.println("Server started on port: " + port); // todo remove or log
    connectionListenerThread.execute(this::listenForClients);
  }

  // todo add shutdown scheme
  public synchronized void shutdown() throws IOException {
    handlerLock.writeLock().lock();
    try {
      server.close();
      clientThreads.shutdown();
      connectionListenerThread.shutdown();
    } finally {
      handlerLock.writeLock().unlock();
    }
  }

  private void listenForClients() {
    try {
      while (!server.isClosed() && server.isBound()) {
        Socket newConnection = server.accept();
        // todo log
        clientThreads.execute(() -> handleConnection(newConnection));
      }
    } catch (IOException e) {
      // todo log
    }
  }

  private void handleConnection(Socket client) {
    String response;
    InputStreamReader isr;
    try {
      isr = new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII);
      Request request = RequestReader.readRequest(isr);
      response = getResponse(request);
      respondToClient(response, client);
    } catch (IOException e) {
      // todo log here?
    } catch (RequestException e) {
      response = Responses.getStandardErrorResponse(e);
      respondToClient(response, client);
    }
  }

  private String getResponse(Request request) throws RequestException {
    String uri = request.getRequestLine().getRequestURI();
    handlerLock.readLock().lock();
    try {
      if (handlers.containsKey(uri)) {
        return handlers.get(uri).respond(request);
      } else {
        throw new RequestException("Resource not found: " + uri,
            StatusCode.CLIENT_ERROR_404_NOT_FOUND);
      }
    } catch (IOException e) {
      // todo logging
    } catch (InterruptedException e) {
      // todo logging
    } finally {
      handlerLock.readLock().unlock();
    }
    return null;
  }

  private void respondToClient(String response, Socket client) {
    try {
      OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream(),
          StandardCharsets.US_ASCII);
      osw.write(response);
      osw.flush();
    } catch (IOException e) {
      // todo logging
    }
  }
}
