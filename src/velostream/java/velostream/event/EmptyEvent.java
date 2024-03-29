package velostream.event;

import velostream.interfaces.IEvent;

public final class EmptyEvent extends Event {

  public static final IEvent EMPTY_EVENT = new EmptyEvent();

  public EmptyEvent() {
    setEventName("EMPTY_EVENT");
  }

  @Override
  public boolean isAlive(int ttl) {
    return false;
  }

}
