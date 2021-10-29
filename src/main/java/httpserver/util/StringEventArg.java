package httpserver.util;

public class StringEventArg extends EventArg {
  private final String arg;

  public StringEventArg(String arg) {
    this.arg = arg;
  }

  public static StringEventArg create(String arg) {
    return new StringEventArg(arg);
  }

  @Override
  public String toString() {
    return arg;
  }

  public String getArg() {
    return arg;
  }
}
