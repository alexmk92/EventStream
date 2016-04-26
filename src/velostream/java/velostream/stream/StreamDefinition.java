package velostream.stream;

import com.sun.istack.internal.Nullable;


/**
 * Stream Definition that is used to define the meta-data of a Stream
 *
 * @author Richard Durley
 *         Date: 10/12/13
 *         Time: 21:00
 */
public class StreamDefinition {

  private final String name;
  private final String description;
  private final String timestampfieldname;
  private final int eventTTLSeconds;
  private final int orderBy;
  private final String orderbyField;
  private final String eventWorker;

  public StreamDefinition(String name, @Nullable String description,
      @Nullable String timestampfieldname, int eventsTTLSeconds, int orderBy,
      @Nullable String orderbyField, @Nullable String eventWorker) throws Exception {
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
      this.eventWorker = "velostream.event.PassthroughEventWorker";
    else
      this.eventWorker = Class.forName(eventWorker).newInstance().getClass().getSimpleName();

    this.orderbyField = orderbyField;
    this.orderBy = orderBy;
    this.name = name;
    this.timestampfieldname = timestampfieldname;
    this.description = description;
    this.eventTTLSeconds = eventsTTLSeconds;
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

  public String getEventWorker() {
    return eventWorker;
  }

  @Override
  public String toString() {
    return this.name;

  }


}
