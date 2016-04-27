package velostream.web;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class StreamAPIApp extends Application {

  private static UndertowJaxrsServer server;

  @Override
  public Set<Class<?>> getClasses() {
    HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(StreamAPIResource.class);
    return classes;
  }

  public static void main(String args[]) {
    Undertow.Builder serverBuilder = Undertow.builder().addHttpListener(8081, "127.0.0.1");
    server = new UndertowJaxrsServer().start(serverBuilder);
    DeploymentInfo di = server.undertowDeployment(StreamAPIApp.class);
    di.setContextPath("/");
    di.setDeploymentName("velostream");
    server.deploy(di);
  }
}

