package httpserver.handlers;

import httpserver.Request;
import httpserver.RequestException;
import httpserver.protocol.StatusCode;
import httpserver.util.Responses;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import system.RequestBuilder;

public class SingleInputHandlerTest {

  private static final String POSTKEY = "input";
  private static final String DOMAIN = "domain";
  private static final String URL = "/url";
  private static SingleInputHandler handler;

  @Before
  public void setUp() {
    handler = new DummyHandler();
    handler.setDomain(DOMAIN);
    handler.setUrl(URL);
  }

  @Test
  public void NON_POST_OR_GET_METHOD_THROWS_NOT_ALLOWED() {
    try {
      Request request = RequestBuilder.genRequestWithMethod("DELETE", "input=blank");
      handler.respond(request);
      Assert.fail();
    } catch (IOException e) {
      Assert.fail();
    } catch (RequestException e) {
      StatusCode expected = StatusCode.CLIENT_ERROR_405_METHOD_NOT_ALLOWED;
      Assert.assertEquals(expected, e.getResponseCode());
    }
  }

  @Test
  public void BODY_DOES_NOT_BEGIN_WITH_POSTKEY_EQUALS_THROWS_BAD_REQUEST() {
    try {
      Request request = RequestBuilder.genRequestWithMethod("POST", POSTKEY + " ");
      handler.respond(request);
      Assert.fail();
    } catch (RequestException e) {
      StatusCode expected = StatusCode.CLIENT_ERROR_400_BAD_REQUEST;
      Assert.assertEquals(expected, e.getResponseCode());
    } catch (IOException e) {
      Assert.fail();
    }
  }

  private static class DummyHandler extends SingleInputHandler {

    @Override
    protected String initPostKey() {
      return POSTKEY;
    }

    @Override
    protected String getPostTermResponse(String term) {
      return Responses.getMessage("Success");
    }

    @Override
    protected String getInputTextBoxLabel() {
      return "Input box";
    }

    @Override
    protected String getGetPageTitle() {
      return "Test Single Input Handler";
    }
  }
}