package httpserver.util;

public class EventArg {

  private static final EventArg empty = new EventArg();

  public EventArg() {
  }

  public static EventArg empty() {
    return empty;
  }
}
