package velostream.web;

import org.boon.Boon;
import org.jboss.resteasy.annotations.ResponseObject;
import velostream.StreamAPI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/stream")
public class StreamResource {

  @GET
  @Path("/{streamname}")
  @Produces("application/json")
  public String getAll(@PathParam("streamname") String streamname) {
    return Boon.toJson(StreamAPI.doQuery(streamname, "All", null));
  }

}
