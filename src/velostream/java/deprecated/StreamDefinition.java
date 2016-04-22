package deprecated;

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
    private final EventDefinition nuggEventBuilder;
    private final int timestampfieldindex;
    private final int eventsTimeToLiveSeconds;
    private final IEventWorker nuggetProcessor;
    protected Stream context;

    public void setNuggetContext(Stream stream)
    {this.context= stream;}

    public Stream getNuggetContext()
    {return context;}

    public StreamDefinition(String name, String description, EventDefinition nuggetEventBuilder, int timestampfieldindex, int eventsTimeToLiveSeconds, IEventWorker nuggetProcessor) {
        if (name == null || name.equals(""))
            throw new IllegalArgumentException("name is mandatory");
        if (nuggetEventBuilder ==null)
            throw new IllegalArgumentException("velostream.event definition is mandatory");

        if (timestampfieldindex != -1 || timestampfieldindex >= nuggetEventBuilder.getFields().length)
            throw new IllegalArgumentException("timestamp field must -1 for default timestamp or the field index that is to be used as the timestamp value");

        if (timestampfieldindex != -1 && !(nuggetEventBuilder.getDefaultValues()[timestampfieldindex] instanceof Long))
            throw new IllegalArgumentException("timestamp field must be defined as a double");

        if (nuggetProcessor == null)
            throw new IllegalArgumentException("NuggetProcessor cannot be null");

        this.name = name;
        this.nuggEventBuilder = nuggetEventBuilder;
        this.timestampfieldindex = timestampfieldindex;
        this.description = description;
        this.nuggetProcessor = nuggetProcessor;
        this.eventsTimeToLiveSeconds = eventsTimeToLiveSeconds;
    }


    public int getTimestampFieldIndex() {
        return this.timestampfieldindex;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public IEventWorker getNuggetProcessor() {
        return this.nuggetProcessor;
    }

    public int getEventsTimeToLiveSeconds() {
        return this.eventsTimeToLiveSeconds;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{nugget_name:");
        str.append(this.name);
        str.append(",nugget_description:");
        str.append(this.description);
        str.append(",nugget_processor:");
        str.append(this.nuggetProcessor.getClass().getSimpleName());
        str.append(",timestamp_field_index:");
        str.append(this.timestampfieldindex);
        str.append(",fields:{");
        for (int i = 0; i < (nuggEventBuilder.getFields().length); i++) {
            str.append("field:{");
            str.append("name:");
            str.append(nuggEventBuilder.getFields()[i].getName());
            str.append("type:");
            str.append(nuggEventBuilder.getDefaultValues()[i].getClass().getSimpleName());
            str.append(",defaultvalue:");
            str.append(nuggEventBuilder.getDefaultValues()[i]);
            str.append("},");
        }
        str.deleteCharAt(str.length() - 1);
        str.append("}}}");
        return str.toString();
    }


}
