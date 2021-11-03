package httpserver;

import httpserver.protocol.StatusCode;
import httpserver.util.Event;
import httpserver.util.Responses;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A simple HTTP server that accepts HTTP/1.1 request messages and sends them to an attached handler
 * based on the request URI.
 *
 * @author Jason McGowan
 */
public class Server {

  private final HashMap<String, Handler> handlers = new HashMap<>();
  private final ReentrantReadWriteLock handlerLock = new ReentrantReadWriteLock();
  private final ExecutorService connectionListenerThread = Executors.newSingleThreadExecutor();
  private final ExecutorService clientThreads = Executors.newCachedThreadPool();
  private final Event<String> logEvent = new Event<>();

  private final String domain;

  private ServerSocket serverSocket;

  public Server(String domain) {
    this.domain = domain;
  }

  public void addMapping(String url, Handler handler) {
    handlerLock.writeLock().lock();
    try {
      handlers.put(url, handler);
      logEvent.invoke(this, "Added handler with URL: " + url);
    } finally {
      handlerLock.writeLock().unlock();
    }
    handler.setDomain(domain);
    handler.setUrl(url);
  }

  public synchronized void start(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    logEvent.invoke(this, "Server started on port: " + port);
    connectionListenerThread.execute(this::listenForClients);
  }

  public synchronized void shutdown() throws IOException {
    logEvent.invoke(this, "Server shutdown requested");
    try {
      serverSocket.close();
      connectionListenerThread.shutdown();
      if (!connectionListenerThread.awaitTermination(5, TimeUnit.SECONDS)) {
        connectionListenerThread.shutdownNow();
      }
      clientThreads.shutdown();
      if (!clientThreads.awaitTermination(5, TimeUnit.SECONDS)) {
        clientThreads.shutdownNow();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public Event<String> getLogEvent() {
    return logEvent;
  }

  public String getDomain() {
    return domain;
  }

  private void listenForClients() {
    try {
      while (!serverSocket.isClosed() && serverSocket.isBound()) {
        Socket newConnection = serverSocket.accept();
        logEvent.invoke(this, "Client connected: " + newConnection);
        clientThreads.execute(() -> handleConnection(newConnection));
      }
    } catch (IOException e) {
      if (!serverSocket.isClosed()) {
        logIOException(e);
      }
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
      logIOException(e);
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
      logIOException(e);
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
      logIOException(e);
    }
  }

  private void logIOException(IOException e) {
    logEvent.invoke(this, "IO Exception: " + e);
  }
}