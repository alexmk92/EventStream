package velostream.util;

import velostream.interfaces.IEventWorker;
import velostream.stream.StreamDefinition;

public class StreamDefinitionBuilder {

  StreamDefinition streamDefinition;
  StreamDefinitionBuilder streamDefinitionBuilder;

  public StreamDefinitionBuilder(String streamName) {
    this.streamDefinition = new StreamDefinition(streamName);
  }

  public static StreamDefinitionBuilder builder(String streamName) {
    return new StreamDefinitionBuilder(streamName);
  }

  public StreamDefinitionBuilder setDescription(String description) {
    this.streamDefinition.setDescription(description);
    return this;
  }

  public StreamDefinitionBuilder setEventTTL(int eventTTL) {
    this.streamDefinition.setEventTTLSeconds(eventTTL);
    return this;
  }

  public StreamDefinitionBuilder addEventWorkerByClassName(String name) throws Exception
  {
    this.streamDefinition.setEventWorkerClassName(name);
    return this;
  }
  public StreamDefinitionBuilder addEventWorker(IEventWorker eventWorker)
  {
    this.streamDefinition.setEventWorker(eventWorker);
    return this;
  }

  public StreamDefinitionBuilder addEventWorkerParam(String paramName, Object value) {
    this.streamDefinition.getWorkerParams().put(paramName, value);
    return this;
  }

  public StreamDefinition build() {
    return this.streamDefinition;
  }
}
