import events.QuoteEvent;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.boon.json.ObjectMapper;
import org.boon.json.ObjectMapperFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.*;

import static org.hamcrest.core.Is.is;

import velostream.StreamAPI;
import velostream.stream.StreamDefinition;
import velostream.stream.Stream;
import velostream.web.StreamAPIApp;
import velostream.web.StreamAPIResource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class TestWebAPI {

  private static UndertowJaxrsServer server;

  @BeforeClass
  public static void setup() throws Exception {
    //given
    Undertow.Builder serverBuilder =
        Undertow.builder().addHttpListener(8081, "127.0.0.1").setWorkerThreads(4);
    server = new UndertowJaxrsServer().start(serverBuilder);
    DeploymentInfo di = server.undertowDeployment(StreamAPIApp.class);
    di.setContextPath("/");
    di.setDeploymentName("velostream");
    server.deploy(di);
    StreamDefinition sd =
        new StreamDefinition("quote", null, null, 1, StreamAPI.ORDERBY_UNORDERED, null,
            "velostream.event.PassthroughEventWorker", null);
    Stream quotestream = StreamAPI.newStream(sd);
    StreamAPI.put("quote", new QuoteEvent("IBM", 2.0d), false);
    StreamAPI.put("quote", new QuoteEvent("IBM", 3.0d), false);
    StreamAPI.put("quote", new QuoteEvent("IBM", 4.0d), false);
    StreamAPI.put("quote", new QuoteEvent("IBM", 5.0d), false);
    StreamAPI.put("quote", new QuoteEvent("IBM", 6.0d), false);
    StreamAPI.put("quote", new QuoteEvent("IBM", 7.0d), false);

    Thread.currentThread().sleep(1000);
  }


  @Test
  public void testGetAll() throws Exception {

    Client client = ClientBuilder.newClient();
    try {
      System.out.println(TestPortProvider.generateURL("/stream/quote/All").toString());
      String val = client.target(TestPortProvider.generateURL("/stream/quote/All")).request()
          .get(String.class);
      System.out.println(val);
    } finally {
      client.close();
    }

  }

  @Test
  public void testGetAverage() throws Exception {
    Client client = ClientBuilder.newClient();
    try {
      String val =
          client.target(TestPortProvider.generateURL("/stream/quote/Average/Quote")).request()
              .get(String.class);
      System.out.println(val);
    } finally {
      client.close();
    }

  }

  @Test
  public void postOne() throws Exception {
    Client client = ClientBuilder.newClient();
    try {
      String input = "{\"symbol\":\"IBM\",\"quote\":2.0}";
      Response response = client.target(TestPortProvider.generateURL("/stream/quote")).request()
          .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
      response.getStatus();
    } finally {
      client.close();
    }
  }

  @Test
  public void post1000() throws Exception {
    Client client = ClientBuilder.newClient();
    try {
      double n = 2.0;
      for (int i = 0; i < 1000; i++) {
        String input = "{\"symbol\":\"IBM\",\"quote\":" + n + "}";
        Response response = client.target(TestPortProvider.generateURL("/stream/quote")).request()
            .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
        response.getStatus();
        n += 2;
        response.close();
      }
    } finally {
      client.close();
    }
  }

  @Test
  public void post1000AsJson() throws Exception {
    Client client = ClientBuilder.newClient();

    try {
      double n = 2.0;
      for (int i = 0; i < 1000; i++) {
        String input = "{\"symbol\":\"IBM\",\"quote\":" + n + "}";
        n += 0.1;
        Response response =
            client.target(TestPortProvider.generateURL("/stream/quote")).request()
                .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
        response.getStatus();
        response.close();
      }
    } finally {
      client.close();
    }
  }

  @Test
  public void testCreateStream() throws Exception {
    //given
    StreamDefinition sd =
        new StreamDefinition("newstream", null, null, 1, StreamAPI.ORDERBY_TIMESTAMP, null, null,
            null);

    Client client = ClientBuilder.newClient();

    try {
      //when
      Response response = client.target(TestPortProvider.generateURL("/stream")).request()
          .post(Entity.entity(sd, MediaType.APPLICATION_JSON_TYPE));
      //then
      Assert.assertThat(response.getStatus(), is(201));
      Assert.assertNotNull(StreamAPI.getStream("newstream"));
    } finally {
      client.close();
    }


  }

  @Test
  public void testWebAPIBenchmark() throws Exception {
    //given

    testCreateStream();
    Client client = ClientBuilder.newClient();
    try {
      double n = 2.0;
      for (int i = 0; i < 40000; i++) {
        String input = "{\"symbol\":\"IBM\",\"quote\":" + n + "}";
        Response response =
            client.target(TestPortProvider.generateURL("/stream/newstream")).request()
                .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
        response.getStatus();
        n += 2;
        response.close();
      }
    } finally {
      client.close();
    }

  }

  @Test
  public void testTestSimpleFilter() throws Exception {

    //given
    Map<String, Object> event_fields = new HashMap<>();
    event_fields.put("field", "delivery_status");
    event_fields.put("operator", "?");
    event_fields.put("value", "dispatched");

    StreamDefinition sd =
        new StreamDefinition("orderdeliverystream", "orderdeliverystream", null, 1,
            StreamAPI.ORDERBY_TIMESTAMP, null, "velostream.event.SimpleFilterEventWorker",
            event_fields);

    Client client = ClientBuilder.newClient();

    try {
      //when
      Response response = client.target(TestPortProvider.generateURL("/stream")).request()
          .post(Entity.entity(sd, MediaType.APPLICATION_JSON_TYPE));
      //then
      Assert.assertThat(response.getStatus(), is(201));
      Assert.assertNotNull(StreamAPI.getStream("orderdeliverystream"));
      response.close();

      String input = "{\"customer\":\"12345\",\"delivery_status\":" + "\"dispatched\"" + "}";
      response =
          client.target(TestPortProvider.generateURL("/stream/orderdeliverystream")).request()
              .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
      response.getStatus();
      response.close();

      String val =
          client.target(TestPortProvider.generateURL("/stream/orderdeliverystream")).request()
              .get(String.class);
      System.out.println(val);


    } finally {
      client.close();
    }


  }

  @AfterClass
  public static void teardown() throws Exception {
    server.stop();
  }


}
