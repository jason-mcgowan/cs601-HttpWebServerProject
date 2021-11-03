package httpserver.handlers;

import httpserver.util.Event;
import httpserver.util.Responses;

public class ShutdownHandler extends SingleInputHandler {

  private final String shutdownKey;
  private final Event<Object> shutdownRequested = new Event<>();

  public ShutdownHandler(String shutdownKey) {
    this.shutdownKey = shutdownKey;
  }

  public Event<Object> getShutdownRequested() {
    return shutdownRequested;
  }

  @Override
  protected String initPostKey() {
    return "input";
  }

  @Override
  protected synchronized String getPostTermResponse(String term) {
    if (!term.equals(shutdownKey)) {
      return Responses.getMessage("Incorrect key");
    }
    try {
      return Responses.getMessage("Server shutdown command issued");
    } finally {
      shutdownRequested.invoke(this, new Object());
    }
  }

  @Override
  protected String getInputTextBoxLabel() {
    return "Enter shutdown key";
  }

  @Override
  protected String getGetPageTitle() {
    return "Shutdown";
  }
}
