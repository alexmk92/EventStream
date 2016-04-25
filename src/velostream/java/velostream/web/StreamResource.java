package velostream.web;

import org.boon.json.ObjectMapper;
import org.boon.json.ObjectMapperFactory;
import velostream.StreamAPI;
import velostream.event.Event;
import velostream.deprecated.StreamDefinition;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.Map;

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
      Event event;
      try {
        //Map to specific event class
        Event eventClass = ((Event) Class.forName(eventClassName).newInstance());
        event = mapper.fromJson(body, eventClass.getClass());
        event.setId(eventClass.getId());
        event.setTimestamp(eventClass.getTimestamp());
      }
      catch (Exception e) {
        //Map to key value list
        Map<String, Object> event_fields = mapper.fromJson(body, Map.class);
        event = new Event(event_fields);
      }

      StreamAPI.put(streamname, event, false);
    } catch (Exception e) {
      throw new BadRequestException();
    }
  }


  @POST
  @Path("/")
  @Consumes("application/json")
  public void create(String body) {
    StreamDefinition streamDefinition = mapper.fromJson(body, StreamDefinition.class);
    StreamAPI.newStream(streamDefinition.getName(),null,StreamAPI.WORKER_RESULTS_UNORDERED,0);
  }

}
