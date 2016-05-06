package WebAPITesting;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.*;

import static org.hamcrest.core.Is.is;

import velostream.StreamAPI;
import velostream.event.Event;
import velostream.util.EventBuilder;
import velostream.stream.workers.SimpleFilterEventWorker;
import velostream.stream.StreamDefinition;
import velostream.stream.Stream;
import velostream.util.StreamDefinitionBuilder;
import velostream.web.StreamAPIApp;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class TestWebAPI {

  private static UndertowJaxrsServer server;
  private static Stream quotestream;

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


    quotestream = StreamAPI.newStream(
        StreamDefinitionBuilder.builder("quote")
            .setEventTTL(1).build());
    Event event =
        EventBuilder.eventBuilder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 20.0D)
            .build();
    quotestream.put(event, false);
    Thread.currentThread().sleep(1000);
  }

  @Test
  public void testGetAll() throws Exception {
    Client client = ClientBuilder.newClient();
    try {
      System.out.println(TestPortProvider.generateURL("/stream/quote/All").toString());
      Event[] eventList = client.target(TestPortProvider.generateURL("/stream/quote/All")).request()
          .get(Event[].class);

      for (Event event : eventList) {
        System.out.println(event.toString());
      }
      System.out.println(eventList.length + ":" + quotestream.getEventQueryStore().getQueryOperations().getCount());
    } finally {
      client.close();
    }

  }

  @Test
  public void testGetAverage() throws Exception {
    Client client = ClientBuilder.newClient();
    try {
      String val =
          client.target(TestPortProvider.generateURL("/stream/quote/Average/price")).request()
              .get(String.class);
      System.out.println("Average=" + val);
    } finally {
      client.close();
    }

  }

  @Test
  public void postOne() throws Exception {
    Client client = ClientBuilder.newClient();
    try {
      Event event =
          EventBuilder.eventBuilder("quote").addFieldValue("symbol", "IBM").addFieldValue("price", 2.0D)
              .build();
      Response response = client.target(TestPortProvider.generateURL("/stream/quote")).request()
          .post(Entity.entity(event, MediaType.APPLICATION_JSON_TYPE));
      response.getStatus();
    } finally {
      client.close();
    }
    this.testGetAll();
  }

  @Test
  public void post5000() throws Exception {
    Client client = ClientBuilder.newClient();
    try {
      double n = 2.0;
      for (int i = 0; i < 5000; i++) {
        Event event =
            EventBuilder.eventBuilder("quote").addFieldValue("symbol", "IBM").addFieldValue("price", n)
                .build();
        Response response = client.target(TestPortProvider.generateURL("/stream/quote")).request()
            .post(Entity.entity(event, MediaType.APPLICATION_JSON_TYPE));
        response.getStatus();
        n += 2;
        response.close();
      }
    } finally {
      client.close();
    }
    this.testGetAll();
  }

  @Test
  public void testCreateStream() throws Exception {
    //given

    StreamDefinition sd =
        StreamDefinitionBuilder.builder("newstream")
            .setEventTTL(1).build();

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
  public void testTestSimpleFilter() throws Exception {

    String ORDER_DELIVERY_STREAM = "orderdeliverystream";

    //given
    Map<String, Object> event_fields = new HashMap<>();
    event_fields.put("field", "delivery_status");
    event_fields.put("operator", "?");
    event_fields.put("value", "dispatched");

    StreamDefinition sd = StreamDefinitionBuilder.builder(ORDER_DELIVERY_STREAM)
        .addEventWorker(new SimpleFilterEventWorker())
        .addEventWorkerParam("field", "delivery_status").addEventWorkerParam("operator", "?")
        .addEventWorkerParam("value", "dispatched").build();

    Client client = ClientBuilder.newClient();

    try {
      //when
      Response response = client.target(TestPortProvider.generateURL("/stream")).request()
          .post(Entity.entity(sd, MediaType.APPLICATION_JSON_TYPE));
      //then
      Assert.assertThat(response.getStatus(), is(201));
      Assert.assertNotNull(StreamAPI.getStream(ORDER_DELIVERY_STREAM));
      response.close();

      Event eventInFilter =
          EventBuilder.eventBuilder(ORDER_DELIVERY_STREAM).addFieldValue("customer", "123456")
              .addFieldValue("delivery_status", "dispatched").build();

      Event eventNotInFilter =
          EventBuilder.eventBuilder(ORDER_DELIVERY_STREAM).addFieldValue("customer", "123456")
              .addFieldValue("delivery_status", "delivered").build();

      response =
          client.target(TestPortProvider.generateURL("/stream/" + ORDER_DELIVERY_STREAM)).request()
              .post(Entity.entity(eventInFilter, MediaType.APPLICATION_JSON_TYPE));
      response.getStatus();

      response.close();

      response =
          client.target(TestPortProvider.generateURL("/stream/" + ORDER_DELIVERY_STREAM)).request()
              .post(Entity.entity(eventNotInFilter, MediaType.APPLICATION_JSON_TYPE));
      response.getStatus();

      response.close();


      String val =
          client.target(TestPortProvider.generateURL("/stream/" + ORDER_DELIVERY_STREAM) + "/All")
              .request().get(String.class);
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
