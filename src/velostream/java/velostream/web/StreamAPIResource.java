package velostream.web;

import velostream.StreamAPI;
import velostream.stream.StreamDefinition;
import velostream.event.Event;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import static velostream.StreamAPI.stream;
import static velostream.StreamAPI.queryStream;


@Path("/stream")
public class StreamAPIResource {

  @GET
  @Path("/{streamname}/{querytype}")
  @Produces("application/json")
  public Object getAll(@PathParam("streamname") String streamname,
      @PathParam("querytype") String querytype) {
    return queryStream(streamname, querytype, null);
  }

  @GET
  @Path("/{streamname}/{querytype}/{fieldname}")
  @Produces("application/json")
  public Object getAvg(@PathParam("streamname") String streamname,
      @PathParam("querytype") String querytype, @PathParam("fieldname") String fieldname) {
    return queryStream(streamname, querytype, fieldname);
  }

  @GET
  @Path("/{streamname}/{querytype}/{fieldname}/{value}")
  @Produces("application/json")
  public Object getQueryBy(@PathParam("streamname") String streamname,
      @PathParam("querytype") String querytype, @PathParam("fieldname") String fieldname,  @PathParam("value") String value) {
    return queryStream(streamname, querytype, fieldname, value);
  }

  @POST
  @Path("/{streamname}")
  @Consumes("application/json")
  public Response postOne(@PathParam("streamname") String streamname, Event event) {
    try {
      stream(streamname).put(event, false);
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
