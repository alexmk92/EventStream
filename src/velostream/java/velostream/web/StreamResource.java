package velostream.web;

import org.boon.json.ObjectMapper;
import org.boon.json.ObjectMapperFactory;
import velostream.StreamAPI;
import velostream.event.Event;
import velostream.interfaces.IEvent;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/stream")
public class StreamResource {

  public static ObjectMapper mapper = ObjectMapperFactory.create();

  @GET
  @Path("/{streamname}/{querytype}")
  @Produces("application/json")
  public String getAll(@PathParam("streamname") String streamname,
      @PathParam("querytype") String querytype) {
    return mapper.toJson((StreamAPI.doQuery(streamname, querytype, null)));
  }

  @GET
  @Path("/{streamname}/{querytype}/{fieldname}")
  @Produces("application/json")
  public String getAvg(@PathParam("streamname") String streamname,
      @PathParam("querytype") String querytype, @PathParam("fieldname") String fieldname) {
    return mapper.toJson((StreamAPI.doQuery(streamname, querytype, fieldname)));
  }

  @POST
  @Path("/{streamname}")
  @Consumes("application/json")
  public void postOne(@PathParam("streamname") String streamname, String body) {
    try {
      StreamAPI.getStream(streamname);
      String eventClassName =
          "events." + streamname.substring(0, 1).toUpperCase() + streamname.substring(1) + "Event";
      Event eventClass = ((Event) Class.forName(eventClassName).newInstance());
      Event event = mapper.fromJson(body, eventClass.getClass());
      event.setId(eventClass.getId());
      event.setTimestamp(eventClass.getTimestamp());
      StreamAPI.put(streamname, event, false);
    } catch (Exception e) {
      throw new BadRequestException();
    }
  }


}
