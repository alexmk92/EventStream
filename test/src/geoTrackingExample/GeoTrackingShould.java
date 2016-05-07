package geoTrackingExample;

import geoTrackingExample.domain.AlertType;
import geoTrackingExample.domain.Journey;
import geoTrackingExample.domain.Stop;
import geoTrackingExample.worker.GeoEventWorker;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

import velostream.StreamAPI;
import velostream.stream.Stream;
import velostream.util.EventBuilder;
import velostream.util.StreamDefinitionBuilder;
import velostream.web.StreamAPIApp;

import static velostream.StreamAPI.stream;

public class GeoTrackingShould {


  static long FIFTEEN_MINUTES_Milliseconds = 15 * 60 * 1000;

  private static Journey journey = new Journey();
  private static UndertowJaxrsServer server;
  private static Stream quotestream;
  private static GeoEventWorker geoWorker;

  @BeforeClass
  public static void setup() {
    setupUndertow();
    StreamAPI.newStream(StreamDefinitionBuilder.streamDefinition("GeoAlert").setEventTTL(60 * 60)
        .addEventWorker(geoWorker = new GeoEventWorker()).build());
    setupJourney();
    geoWorker.setJourney(journey);
  }

  public static void setupUndertow() {
    Undertow.Builder serverBuilder =
        Undertow.builder().addHttpListener(8081, "127.0.0.1").setWorkerThreads(4);
    server = new UndertowJaxrsServer().start(serverBuilder);
    DeploymentInfo di = server.undertowDeployment(StreamAPIApp.class);
    di.setContextPath("/");
    di.setDeploymentName("velostream");
    server.deploy(di);
  }

  public static void setupJourney() {
    Stop stop1 =
        new Stop("Laurence", 51.509041, -0.030198, System.currentTimeMillis() + 16 * 60 * 1000);
    Stop stop2 =
        new Stop("Rich", 51.513545, -0.027418, System.currentTimeMillis() + 26 * 60 * 1000);
    Stop stop3 =
        new Stop("Sandeep", 51.515618, -0.038754, System.currentTimeMillis() + 42 * 60 * 1000);
    Stop stop4 =
        new Stop("Sheryl", 51.529399, -0.067545, System.currentTimeMillis() + 55 * 60 * 1000);
    Stop stop5 =
        new Stop("Louise", 51.543092, -0.082458, System.currentTimeMillis() + 75 * 60 * 1000);
    journey.add(stop1);
    journey.add(stop2);
    journey.add(stop3);
    journey.add(stop4);
    journey.add(stop5);
  }

  @Test
  public void returnONTIMEWhenVehicleCanReachDeliveryGEOOnTime() throws Exception {
    stream("GeoAlert").put(
        EventBuilder.eventBuilder("vangeo").addFieldValue("lat", 51.49).addFieldValue("lon", -0.07)
            .addFieldValue("van_id", 1).addFieldValue("avg_speed", 20.0d).build(), false);
    Thread.currentThread().sleep(200);
    Assert.assertThat(stream("GeoAlert").query()
        .getLastBy("customerId", "Laurence").getFieldValue("status"), is(AlertType.ONTIME));
  }

  @Test
  public void returnLATEWhenVehicleCannotReachDeliveryGEOOnTime() throws Exception {
    geoWorker.setAvg_roadspeed_KMH(1);
    stream("GeoAlert").put(
        EventBuilder.eventBuilder("vangeo").addFieldValue("lat", 51.49).addFieldValue("lon", -0.07)
            .addFieldValue("van_id", 1).addFieldValue("avg_speed", 1.0d).build(), false);
    Thread.currentThread().sleep(200);
    Assert.assertThat(stream("GeoAlert").query()
        .getLastBy("customerId", "Laurence").getFieldValue("status"), is(AlertType.LATE));
  }


}
