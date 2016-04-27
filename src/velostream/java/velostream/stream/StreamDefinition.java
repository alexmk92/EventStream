package velostream.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.Nullable;
import velostream.event.PassthroughEventWorker;
import velostream.interfaces.IEventWorker;

import java.util.Map;


/**
 * Stream Definition that is used to define the meta-data of a Stream
 *
 * @author Richard Durley
 *         Date: 10/12/13
 *         Time: 21:00
 */
public class StreamDefinition {

  private String name;
  private String description;
  private String timestampfieldname;
  private int eventTTLSeconds;
  private int orderBy;
  private String orderbyField;
  private String eventWorkerName;
  private Map<String, Object> workerParams;

  public StreamDefinition() {
    super();
  }

  public StreamDefinition(String name, @Nullable String description,
      @Nullable String timestampfieldname, int eventsTTLSeconds, int orderBy,
      @Nullable String orderbyField, @Nullable String eventWorker,
      Map<String, Object> workerParams) {
    if (name == null || name.equals(""))
      throw new IllegalArgumentException("name is mandatory");
    if (eventsTTLSeconds < 0)
      throw new IllegalArgumentException("eventTTLSeconds must be >=0");
    if (description == null)
      description = "no description provided";
    if (timestampfieldname == null || timestampfieldname.isEmpty())
      timestampfieldname = "Timestamp";
    if (orderBy < 0 || orderBy > 3)
      throw new IllegalArgumentException("incorrect orderby value");
    if (eventWorker == null)
      this.eventWorkerName = "velostream.event.PassthroughEventWorker";
    else
      this.eventWorkerName = eventWorker;


    this.orderbyField = orderbyField;
    this.orderBy = orderBy;
    this.name = name;
    this.timestampfieldname = timestampfieldname;
    this.description = description;
    this.eventTTLSeconds = eventsTTLSeconds;
    this.workerParams = workerParams;
  }

  public String getName() {
    return this.name;
  }

  public int getOrderBy() {
    return orderBy;
  }

  public String getDescription() {
    return this.description;
  }

  public int getEventTTLSeconds() {
    return this.eventTTLSeconds;
  }

  public String getTimestampfieldname() {
    return timestampfieldname;
  }

  public String getOrderbyField() {
    return orderbyField;
  }

  public String getEventWorkerName() {
    return eventWorkerName;
  }

  public Map<String, Object> getWorkerParams() {
    return workerParams;
  }

  @JsonIgnore
  public IEventWorker getEventWorker() {
    try {
      return (IEventWorker) Class.forName(this.eventWorkerName).newInstance();
    } catch (Exception e) {
      return new PassthroughEventWorker();
    }

  }

  @Override
  public String toString() {
    return this.name;

  }


}
