package velostream.deprecated;

import com.sun.istack.internal.Nullable;
import velostream.infrastructure.Stream;
import velostream.interfaces.IEventWorker;


/**
 * StreamResource Definition that is used to define the meta-data of a StreamResource
 *
 * @author Richard Durley
 *         Date: 10/12/13
 *         Time: 21:00
 */
public class StreamDefinition {

  private final String name;
  private final String description;
  private EventDefinition eventDefinition;
  private final String timestampfieldname;
  private final int eventsTimeToLiveSeconds;
  private final IEventWorker eventWorker;
  protected Stream context;

  public StreamDefinition(String name, String description, EventDefinition eventDefinition,
      @Nullable String timestampfieldname, int eventsTTLSeconds, String eventWorkerName)
      throws Exception {
    if (name == null || name.equals(""))
      throw new IllegalArgumentException("name is mandatory");
    if (eventDefinition == null)
      throw new IllegalArgumentException("event definition is mandatory");

    this.name = name;
    this.eventDefinition = eventDefinition;
    this.timestampfieldname = timestampfieldname;
    this.description = description;
    this.eventWorker = (IEventWorker) Class.forName(eventWorkerName).newInstance();
    this.eventsTimeToLiveSeconds = eventsTTLSeconds;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public IEventWorker getEventWorker() {
    return this.eventWorker;
  }

  public int getEventsTimeToLiveSeconds() {
    return this.eventsTimeToLiveSeconds;
  }

  @Override
  public String toString() {
    return this.name;

  }


}
