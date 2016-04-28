package velostream.web;

import velostream.StreamAPI;
import velostream.stream.StreamDefinition;
import velostream.event.Event;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("/stream")
public class StreamAPIResource {

  @GET
  @Path("/{streamname}/{querytype}")
  @Produces("application/json")
  public Object getAll(@PathParam("streamname") String streamname,
      @PathParam("querytype") String querytype) {
    return StreamAPI.doQuery(streamname, querytype, null);
  }

  @GET
  @Path("/{streamname}/{querytype}/{fieldname}")
  @Produces("application/json")
  public Object getAvg(@PathParam("streamname") String streamname,
      @PathParam("querytype") String querytype, @PathParam("fieldname") String fieldname) {
    return StreamAPI.doQuery(streamname, querytype, fieldname);
  }

  @POST
  @Path("/{streamname}")
  @Consumes("application/json")
  public Response postOne(@PathParam("streamname") String streamname, Event event) {
    try {
      StreamAPI.getStream(streamname);
      StreamAPI.put(streamname, event, false);
      return Response.status(201).build();
    } catch (Exception e) {
      throw new BadRequestException(e);
    }
  }


  @POST
  @Path("/")
  @Consumes("application/json")
  public Response create(StreamDefinition streamDefinition) {
    try {
      StreamAPI.newStream(streamDefinition);
      return Response.status(201).build();
    }
    catch (Exception e) {
      throw new BadRequestException(e);
    }

  }

}
