package WebAPITesting;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import velostream.StreamAPI;
import velostream.event.Event;
import velostream.stream.Stream;
import velostream.stream.StreamDefinition;
import velostream.stream.workers.SimpleFilterEventWorker;
import velostream.web.StreamAPIApp;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static velostream.StreamAPI.newStream;
import static velostream.util.EventBuilder.eventBuilder;
import static velostream.util.StreamDefinitionBuilder.streamDefinition;

public class WebAPIShould {

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


    quotestream = newStream(streamDefinition("quote").setEventTTL(1).build());
    Event event = eventBuilder("quote").addFieldValue("symbol", "JRD")
        .addFieldValue("price", 20.0D).build();
    quotestream.put(event, false);
    quotestream.query().getAverage("price");
    Thread.currentThread().sleep(100);
  }

  @Test
  public void getAll() throws Exception {
    Client client = ClientBuilder.newClient();
    try {
      Event[] eventList = client.target(TestPortProvider.generateURL("/stream/quote/All")).request()
          .get(Event[].class);

      for (Event event : eventList) {
        System.out.println(event.toString());
      }
      System.out.println(eventList.length + ":" + quotestream.query().getCount());
    } finally {
      client.close();
    }

  }

  @Test
  public void getAverage() throws Exception {
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
      Event event = eventBuilder("quote").addFieldValue("symbol", "IBM")
          .addFieldValue("price", 2.0D).build();
      Response response = client.target(TestPortProvider.generateURL("/stream/quote")).request()
          .post(Entity.entity(event, MediaType.APPLICATION_JSON_TYPE));
      response.getStatus();
    } finally {
      client.close();
    }
    this.getAll();
    this.getAverage();
  }

  @Test
  public void post5000() throws Exception {
    Client client = ClientBuilder.newClient();
    try {
      double n = 2.0;
      for (int i = 0; i < 5000; i++) {
        Event event = eventBuilder("quote").addFieldValue("symbol", "IBM")
            .addFieldValue("price", n).build();
        Response response = client.target(TestPortProvider.generateURL("/stream/quote")).request()
            .post(Entity.entity(event, MediaType.APPLICATION_JSON_TYPE));
        response.getStatus();
        n += 2;
        response.close();
      }
    } finally {
      client.close();
    }
    this.getAll();
    this.getAverage();
  }

  @Test
  public void createStream() throws Exception {
    //given

    StreamDefinition sd = streamDefinition("newstream").setEventTTL(1).build();

    Client client = ClientBuilder.newClient();

    try {
      //when
      Response response = client.target(TestPortProvider.generateURL("/stream")).request()
          .post(Entity.entity(sd, MediaType.APPLICATION_JSON_TYPE));
      //then
      Assert.assertThat(response.getStatus(), is(201));
      Assert.assertNotNull(StreamAPI.stream("newstream"));
    } finally {
      client.close();
    }


  }

  @Test
  public void doSimpleFilter() throws Exception {

    String ORDER_DELIVERY_STREAM = "orderdeliverystream";

    StreamDefinition sd = streamDefinition(ORDER_DELIVERY_STREAM)
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
      Assert.assertNotNull(StreamAPI.stream(ORDER_DELIVERY_STREAM));
      response.close();

      Event eventInFilter =
          eventBuilder(ORDER_DELIVERY_STREAM).addFieldValue("customer", "123456")
              .addFieldValue("delivery_status", "dispatched").build();

      Event eventNotInFilter =
          eventBuilder(ORDER_DELIVERY_STREAM).addFieldValue("customer", "123456")
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


      Event[] val =
          client.target(TestPortProvider.generateURL("/stream/" + ORDER_DELIVERY_STREAM) + "/All")
              .request().get(Event[].class);
      assertThat(val[0].getFieldValue("delivery_status"), is("dispatched"));


    } finally {
      client.close();
    }

  }

  @AfterClass
  public static void teardown() throws Exception {
    server.stop();
  }


}
