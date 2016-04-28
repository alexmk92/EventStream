package velostream.stream;

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

  public StreamDefinitionBuilder addEventWorker(Class eventWorkerClassName)
  {
    this.streamDefinition.setEventWorkerName(eventWorkerClassName.getName());
    return this;
  }

  public StreamDefinitionBuilder addEventWorkerParam(String paramName, Object value) {
    this.streamDefinition.getWorkerParams().put(paramName, value);
    return this;
  }

  public StreamDefinitionBuilder setOrderBy(int orderBy) {
    this.streamDefinition.setOrderBy(orderBy);
    return this;
  }

  public StreamDefinitionBuilder setOrderByField(String field) {
    this.streamDefinition.setOrderbyField(field);
    return this;
  }

  public StreamDefinition build() {
    return this.streamDefinition;
  }
}
