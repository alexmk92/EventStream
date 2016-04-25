import events.QuoteEvent;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.boon.Boon;
import org.boon.json.ObjectMapper;
import org.boon.json.ObjectMapperFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.*;
import velostream.StreamAPI;
import velostream.event.Event;
import velostream.event.PassthroughEventWorker;
import velostream.infrastructure.Stream;
import velostream.interfaces.IEventWorker;
import velostream.web.StreamApp;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

public class TestWebAPI {

  private static UndertowJaxrsServer server;

  @BeforeClass
  public static void setup() throws Exception {
    //given
    Undertow.Builder serverBuilder = Undertow.builder().addHttpListener(8081, "127.0.0.1");
    server = new UndertowJaxrsServer().start(serverBuilder);
    DeploymentInfo di = server.undertowDeployment(StreamApp.class);
    di.setContextPath("/");
    di.setDeploymentName("velostream");
    server.deploy(di);
    Stream quotestream = StreamAPI
        .newStream("quote", new PassthroughEventWorker(),
            StreamAPI.WORKER_RESULTS_UNORDERED, 0);
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
    String val = client.target(TestPortProvider.generateURL("/velostream/stream/quote/All")).request()
        .get(String.class);
    System.out.println(val);
    client.close();

  }

  @Test
  public void testGetAverage() throws Exception {
    Client client = ClientBuilder.newClient();
    String val = client.target(TestPortProvider.generateURL("/velostream/stream/quote/Average/Quote")).request()
        .get(String.class);
    System.out.println(val);
    client.close();

  }

  @Test
  public void postOne() throws Exception {
    Client client = ClientBuilder.newClient();
    String input = "{\"symbol\":\"IBM\",\"quote\":2.0}";
    Response response =
        client.target(TestPortProvider.generateURL("/velostream/stream/quote")).request()
            .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
    response.getStatus();
    client.close();

  }

  @Test
  public void post1000() throws Exception {
    Client client = ClientBuilder.newClient();
    double n = 2.0;

    for (int i = 0; i < 1000; i++) {
      String input = "{\"symbol\":\"IBM\",\"quote\":" + n + "}";
      n+=0.1;
      Response response =
          client.target(TestPortProvider.generateURL("/velostream/stream/quote")).request()
              .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
      response.getStatus();
    }
    client.close();
  }

  @Test
  public void post1000AsJson() throws Exception {
    Client client = ClientBuilder.newClient();
    StreamAPI.newStream("quotenew", null, 2, 0);

    //Fixme why is not working from JSON to Object
    HashMap<String, Object> myhash = new HashMap<>();
    myhash.put("symbol", "IBM");
    myhash.put("value", 2.0d);
    Event e = new Event(myhash);
    ObjectMapper mapper = ObjectMapperFactory.create();
    System.out.println(mapper.toJson(myhash));
    Event test = mapper.fromJson(mapper.toJson(myhash), Event.class);

    double n = 2.0;

    for (int i = 0; i < 1000; i++) {
      //String input = "[{\"symbol\":\"IBM\"},{\"quote\":" + n + "}]";
      String input = "{\"symbol\":\"IBM\",\"quote\":" + n +"}";
      n+=0.1;
      Response response =
          client.target(TestPortProvider.generateURL("/velostream/stream/quotenew")).request()
              .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
      response.getStatus();
    }
    client.close();
  }


  @AfterClass
  public static void teardown() throws Exception {
    server.stop();
  }


}
