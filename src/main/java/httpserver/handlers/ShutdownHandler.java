package httpserver.handlers;

import httpserver.util.Event;
import httpserver.util.EventArg;
import httpserver.util.Responses;
import java.io.IOException;

public class ShutdownHandler extends SingleInputHandler {

  private final String shutdownKey;
  private final Event<EventArg> shutdownRequested = new Event<>();

  public ShutdownHandler(String domain, String shutdownKey) {
    super(domain);
    this.shutdownKey = shutdownKey;
  }

  public Event<EventArg> getShutdownRequested() {
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
      shutdownRequested.invoke(this, EventArg.empty());
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
