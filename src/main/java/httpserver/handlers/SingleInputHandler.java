package httpserver.handlers;

import httpserver.Handler;
import httpserver.Method;
import httpserver.Request;
import httpserver.RequestException;
import httpserver.Responses;
import httpserver.StatusCode;
import httpserver.util.HtmlBuilder;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public abstract class SingleInputHandler implements Handler {

  private final String postKey;
  private final String domain;
  private String getResponse;

  public SingleInputHandler(String domain) {
    this.postKey = initPostKey();
    this.domain = domain;
  }

  @Override
  public final void setMapping(String url) {
    getResponse = buildGetResponse(url);
  }

  @Override
  public final String respond(Request request)
      throws RequestException, IOException, InterruptedException {
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

  protected abstract String initPostKey();

  protected abstract String getPostTermResponse(String term)
      throws IOException, InterruptedException;

  protected abstract String getInputTextBoxLabel();

  protected abstract String getGetPageTitle();

  private String postResponse(Request request)
      throws RequestException, IOException, InterruptedException {
    String term = getTermOrThrow(request.getBody());
    return getPostTermResponse(term);
  }

  private String getTermOrThrow(String body) throws RequestException {
    String decoded = URLDecoder.decode(body, StandardCharsets.US_ASCII);
    String keyId = postKey + "=";
    if (!decoded.startsWith(keyId)) {
      throw new RequestException("Message body does not start with key: " + postKey,
          StatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
    int termInd = keyId.length();
    return decoded.substring(termInd).split(" ", 1)[0];
  }

  private String buildGetResponse(String url) {
    String form = HtmlBuilder.inputTextForPost(url, getInputTextBoxLabel(), postKey,
        "Submit");
    String page = HtmlBuilder.simplePage(domain, getGetPageTitle(), form);
    return Responses.getMessage(page);
  }
}