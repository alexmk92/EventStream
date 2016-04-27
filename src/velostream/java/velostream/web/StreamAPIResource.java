package velostream.web;

import org.boon.json.ObjectMapper;
import org.boon.json.ObjectMapperFactory;
import velostream.StreamAPI;
import velostream.stream.StreamDefinition;
import velostream.event.Event;
import velostream.interfaces.IEventWorker;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/stream")
public class StreamAPIResource {

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
  public Response postOne(@PathParam("streamname") String streamname, String body) {
    try {
      StreamAPI.getStream(streamname);
      String eventClassName =
          "events." + streamname.substring(0, 1).toUpperCase() + streamname.substring(1) + "Event";
      Event event;
      try {
        //Map to specific event class
        Event eventClass = ((Event) Class.forName(eventClassName).newInstance());
        event = mapper.fromJson(body, eventClass.getClass());
        event.setEventID(eventClass.getEventID());
        event.setTimestamp(eventClass.getTimestamp());
      }
      catch (Exception e) {
        //Map to key value list
        Map<String, Object> event_fields = mapper.fromJson(body, Map.class);
        event = new Event(event_fields);
      }

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
