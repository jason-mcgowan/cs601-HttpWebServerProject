package httpserver.handlers;

import com.google.gson.JsonObject;
import httpserver.RequestException;
import httpserver.util.Responses;
import httpserver.protocol.StatusCode;
import httpserver.protocol.Version;
import httpserver.util.HtmlBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

public class ChatHandler extends SingleInputHandler {

  private final URI webhookUrl;
  private final String TITLE = "Slack Messenger";
  private final String goodResponse;

  public ChatHandler(String domain, String webhookUrl) {
    super(domain);
    this.webhookUrl = URI.create(webhookUrl);
    goodResponse = HtmlBuilder.simplePage(domain, TITLE, "Post successful");
  }

  @Override
  protected String initPostKey() {
    return "msg";
  }

  @Override
  protected String getInputTextBoxLabel() {
    return "Message to send to Slack USFCS#cs601-project3";
  }

  @Override
  protected String getGetPageTitle() {
    return TITLE;
  }

  @Override
  protected String getPostTermResponse(String term) throws IOException {
    try {
      sendChatMessage(term);
      return Responses.getMessage(goodResponse);
    } catch (InterruptedException e) {
      return Responses.getMessage(Version.HTTP_1_1,
          StatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, "Connection to slack bot interrupted");
    } catch (RequestException e) {
      return Responses.getMessage(Version.HTTP_1_1,
          StatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void sendChatMessage(String term)
      throws IOException, InterruptedException, RequestException {
    String text = URLDecoder.decode(term, StandardCharsets.US_ASCII);
    JsonObject body = new JsonObject();
    body.addProperty("text", text);
    HttpRequest request = HttpRequest.newBuilder(webhookUrl)
        .POST(BodyPublishers.ofString(body.toString()))
        .build();
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request,
        BodyHandlers.ofString(StandardCharsets.US_ASCII));
    if (response.statusCode() != Integer.parseInt(StatusCode.SUCCESS_200_OK.getCode())) {
      throw new RequestException("Error sending message: " + response.body(),
          StatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
    }
  }
}
