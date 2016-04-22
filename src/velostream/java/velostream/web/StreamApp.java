package velostream.web;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/velostream")
public class StreamApp extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(StreamResource.class);
    return classes;
  }
}

