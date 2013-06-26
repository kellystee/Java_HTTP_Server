package http;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RequestTest {

  private Request request;

  @Before
  public void setUp() throws IOException {
    request = new Request();
  }

  @Test
  public void testParseRequestWithQueryString() {
    request.parse("GET /donaldduck.html?text_color=blue HTTP/1.0");
    assertEquals(request.httpMethod, "GET");
    assertEquals(request.fullURL, "/donaldduck.html?text_color=blue");
    assertEquals(request.httpProtocol, "HTTP/1.0");
    assertEquals(request.baseURL, "/donaldduck.html");
    assertEquals(request.queryString, "text_color=blue");
  }

  @Test
  public void testParseRequestWithNoQueryString() {
    request.parse("GET /donaldduck.html HTTP/1.0");
    assertEquals(request.httpMethod, "GET");
    assertEquals(request.fullURL, "/donaldduck.html");
    assertEquals(request.httpProtocol, "HTTP/1.0");
    assertEquals(request.baseURL, "/donaldduck.html");
    assertEquals(request.queryString, null);
  }

}