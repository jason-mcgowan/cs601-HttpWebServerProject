package httpserver;

import httpserver.protocol.StatusCode;
import httpserver.util.Event;
import httpserver.util.EventArg;
import httpserver.util.Responses;
import httpserver.util.StringEventArg;
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

  public static final String POISON_URI = "/47c9d407-fa1b-496d-9437-a2ca67b6b3e6";

  private final HashMap<String, Handler> handlers = new HashMap<>();
  private final ReentrantReadWriteLock handlerLock = new ReentrantReadWriteLock();
  private final ExecutorService connectionListenerThread = Executors.newSingleThreadExecutor();
  private final ExecutorService clientThreads = Executors.newCachedThreadPool();
  private final Event<StringEventArg> logEvent = new Event<>();
  private final Event<EventArg> shutdownEvent = new Event<>();


  private ServerSocket server;

  public Server() {
  }

  public void addMapping(String url, Handler handler) {
    handlerLock.writeLock().lock();
    try {
      handlers.put(url, handler);
      logEvent.invoke(this, new StringEventArg("Added handler with URL: " + url));
    } finally {
      handlerLock.writeLock().unlock();
    }
    handler.setMapping(url);
  }

  public synchronized void start(int port) throws IOException {
    server = new ServerSocket(port);
    logEvent.invoke(this, new StringEventArg("Server started on port: " + port));
    connectionListenerThread.execute(this::listenForClients);
  }

  public synchronized void shutdown() throws IOException {
    logEvent.invoke(this, new StringEventArg("Server shutdown requested"));
    shutdownEvent.invoke(this, EventArg.empty());
    handlerLock.writeLock().lock();
    try {
      server.close();
      clientThreads.shutdown();
      connectionListenerThread.shutdown();
      logEvent.close();
      shutdownEvent.close();
    } finally {
      handlerLock.writeLock().unlock();
    }
  }

  public Event<StringEventArg> getLogEvent() {
    return logEvent;
  }

  public Event<EventArg> getShutdownEvent() {
    return shutdownEvent;
  }


  private void listenForClients() {
    try {
      while (!server.isClosed() && server.isBound()) {
        Socket newConnection = server.accept();
        logEvent.invoke(this, new StringEventArg("Client connected: " + newConnection));
        clientThreads.execute(() -> handleConnection(newConnection));
      }
    } catch (IOException e) {
      if (!server.isClosed()) {
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
      if (request.getRequestLine().getRequestURI().equals(POISON_URI)) {
        shutdown();
        return;
      }
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
    logEvent.invoke(this, new StringEventArg("IO Exception: " + e));
  }

}
