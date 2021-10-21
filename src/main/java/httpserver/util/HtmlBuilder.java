package httpserver.util;

public final class HtmlBuilder {

  private static final String START_TO_NAMESPACE = "<!DOCTYPE HTML><html xmlns =\"";
  private static final String NAMESPACE_TO_TITLE = "\"><head><title>";
  private static final String TITLE_TO_BODY = "</title></head><body>";
  private static final String BODY_TO_END = "</body></html>";

  private HtmlBuilder() {
  }

  public static String simplePage(String namespace, String title, String bodyXml) {
    return START_TO_NAMESPACE + namespace
        + NAMESPACE_TO_TITLE + title
        + TITLE_TO_BODY + bodyXml
        + BODY_TO_END;
  }

}
