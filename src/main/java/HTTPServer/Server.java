package HTTPServer;

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

  public void addMapping(String term, Handler handler) {
    handlerLock.writeLock().lock();
    handlers.put(term, handler);
    handlerLock.writeLock().unlock();
  }

  public synchronized void start(int port) {
    try {
      server = new ServerSocket(port);
      System.out.println("Server started on port: " + port); // todo remove or log
    } catch (IOException e) {
      System.out.println("Error: " + e.getLocalizedMessage()); // todo log
    }
    connectionListenerThread.execute(this::listenForClients);
  }

  private void listenForClients() {
    try {
      while (!server.isClosed() && server.isBound()) {
        Socket newConnection = server.accept();
        System.out.println("New connection from: " + newConnection); // todo log or remove
        clientThreads.execute(() -> handleConnection(newConnection));
      }
    } catch (IOException e) {
      System.out.println("Error connecting"); // todo log
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
            StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
      }
    } finally {
      handlerLock.readLock().unlock();
    }
  }

  private void respondToClient(String response, Socket client) {
    try {
      OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream(),
          StandardCharsets.US_ASCII);
      osw.write(response);
      osw.flush();
      System.out.println(response);
    } catch (IOException e) {
      // todo logging
    }
  }
}
