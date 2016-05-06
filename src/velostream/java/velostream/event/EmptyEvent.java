package velostream.event;

import velostream.interfaces.IEvent;

public final class EmptyEvent extends Event {

  public EmptyEvent() {
    setEventName("EmptyEvent");
  }

  @Override
  public boolean isAlive(int ttl) {
    return false;
  }

  public static final IEvent EmptyEvent = new EmptyEvent();
}
