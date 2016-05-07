package geoTrackingExample;

import geoTrackingExample.domain.Journey;
import geoTrackingExample.domain.Stop;
import geoTrackingExample.worker.GeoEventWorker;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import velostream.StreamAPI;
import velostream.stream.Stream;
import velostream.util.StreamDefinitionBuilder;
import velostream.web.StreamAPIApp;
import velostream.web.StreamAPIResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class GeoAPP extends Application {

  static long FIFTEEN_MINUTES_Milliseconds = 15 * 60 * 1000;
  private static Journey journey = new Journey();
  private static UndertowJaxrsServer server;
  private static Stream quotestream;
  private static GeoEventWorker geoWorker;

  @Override
  public Set<Class<?>> getClasses() {
    HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(StreamAPIResource.class);
    return classes;
  }

  public static void setup() {

    setupJourney();
    geoWorker = new GeoEventWorker();
    StreamAPI.newStream(
        StreamDefinitionBuilder.streamDefinition("GeoAlert").setEventTTL(60*60).addEventWorker(geoWorker)
            .build());
    geoWorker.setJourney(journey);
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

  public static void main(String args[]) throws Exception {
    Undertow.Builder serverBuilder = Undertow.builder().addHttpListener(8081, "127.0.0.1");
    server = new UndertowJaxrsServer().start(serverBuilder);
    DeploymentInfo di = server.undertowDeployment(StreamAPIApp.class);
    di.setContextPath("/");
    di.setDeploymentName("velostream");
    server.deploy(di);
    setup();
    Thread.currentThread().sleep(1000*60*3);
  }
}

