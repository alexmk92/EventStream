package velostream.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.Nullable;
import velostream.event.PassthroughEventWorker;
import velostream.interfaces.IEventWorker;

import java.util.HashMap;
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

  public StreamDefinition (String streamName) {
      this.name=streamName;
      this.description="";
      this.orderBy=StreamOrderBy.ORDERBY_UNORDERED;
      this.orderbyField="Timestamp";
      this.eventTTLSeconds=0;
      this.eventWorkerName=PassthroughEventWorker.class.getName();
      this.workerParams= new HashMap<>();
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

  public void setDescription(String description) {
    this.description = description;
  }

  public void setTimestampfieldname(String timestampfieldname) {
    this.timestampfieldname = timestampfieldname;
  }

  public void setEventTTLSeconds(int eventTTLSeconds) {
    this.eventTTLSeconds = eventTTLSeconds;
  }

  public void setOrderBy(int orderBy) {
    this.orderBy = orderBy;
  }

  public void setOrderbyField(String orderbyField) {
    this.orderbyField = orderbyField;
  }

  public void setEventWorkerName(String eventWorkerName) {
    this.eventWorkerName = eventWorkerName;
  }

  @Override
  public String toString() {
    return this.name;

  }


}
