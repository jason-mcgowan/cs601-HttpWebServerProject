package httpserver.handlers;

import httpserver.Handler;
import httpserver.Method;
import httpserver.Request;
import httpserver.RequestException;
import httpserver.Responses;
import httpserver.StatusCode;
import httpserver.util.HtmlBuilder;

public abstract class SingleInputHandler implements Handler {

  private final String postKey;
  private final String domain;
  private String getResponse;

  public SingleInputHandler(String postKey, String domain) {
    this.postKey = postKey;
    this.domain = domain;
  }

  protected abstract String initPostKey();

  protected abstract String getInputTextBoxLabel();

  protected abstract String postResponse(Request request);

  protected abstract String getGetPageTitle();

  @Override
  public String respond(Request request) throws RequestException {
    Method method = request.getRequestLine().getMethod();
    switch (method) {
      case GET -> {
        return getResponse;
      }
      case POST -> {
        return postResponse(request);
      }
      default -> throw new RequestException("Method not supported: " + method.getId(),
          StatusCode.CLIENT_ERROR_405_METHOD_NOT_ALLOWED);
    }
  }

  @Override
  public void setMapping(String url) {
    getResponse = buildGetResponse(url);
  }

  private String buildGetResponse(String url) {
    String form = HtmlBuilder.inputTextForPost(url, getInputTextBoxLabel(), postKey,
        "Submit");
    String page = HtmlBuilder.simplePage(domain, getGetPageTitle(), form);
    return Responses.getMessage(page);
  }
}