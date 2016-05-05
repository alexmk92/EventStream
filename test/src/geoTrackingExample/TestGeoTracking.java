package geoTrackingExample;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import velostream.StreamAPI;
import velostream.exceptions.StreamNotFoundException;
import velostream.interfaces.IEventWorker;
import velostream.stream.Stream;
import velostream.util.EventBuilder;
import velostream.util.StreamDefinitionBuilder;
import velostream.web.StreamAPIApp;

import java.util.ArrayList;

public class TestGeoTracking {


  static long FIFTEEN_MINUTES_Milliseconds = 15*60*1000;

  private static ArrayList<Stop> stops = new ArrayList<>();
  private static UndertowJaxrsServer server;
  private static Stream quotestream;
  private static GeoEventWorker geoWorker;

  @BeforeClass
  public static void setup() {
    Stop stop1 = new Stop("Laurence", 51.509041, -0.030198, System.currentTimeMillis()+16*60*1000);
    Stop stop2 = new Stop("Rich", 51.513545, -0.027418, System.currentTimeMillis()+26*60*1000);
    Stop stop3 = new Stop("Sandeep", 51.515618,  -0.038754, System.currentTimeMillis()+42*60*1000);
    Stop stop4 = new Stop("Sheryl", 51.529399, -0.067545, System.currentTimeMillis()+55*60*1000);
    Stop stop5 = new Stop("Louise", 51.543092, -0.082458, System.currentTimeMillis()+75*60*1000);

    stops.add(stop1);
    stops.add(stop2);
    stops.add(stop3);
    stops.add(stop4);
    stops.add(stop5);


    //given
    Undertow.Builder serverBuilder =
        Undertow.builder().addHttpListener(8081, "127.0.0.1").setWorkerThreads(4);
    server = new UndertowJaxrsServer().start(serverBuilder);
    DeploymentInfo di = server.undertowDeployment(StreamAPIApp.class);
    di.setContextPath("/");
    di.setDeploymentName("velostream");
    server.deploy(di);

    geoWorker = new GeoEventWorker();
    StreamAPI.newStream(
        StreamDefinitionBuilder.builder("GeoAlert")
            .setEventTTL(30).addEventWorker(geoWorker).build());
    geoWorker.setStops(stops);

  }

  @Test
  public void testOnTime() throws StreamNotFoundException {
    StreamAPI.put("GeoAlert", EventBuilder.builder("vangeo").addFieldValue("lat", 51.49).addFieldValue("lon", -0.07).build(), false);
  }


  @Test
  public void testLate() throws StreamNotFoundException {
    geoWorker.setAvg_roadspeed_KMH(1);
    StreamAPI.put("GeoAlert", EventBuilder.builder("vangeo").addFieldValue("lat", 51.49).addFieldValue("lon", -0.07).build(), false);
  }

  @AfterClass
  public static void sleep() throws Exception {
    Thread.currentThread().sleep(20000);
  }



}
