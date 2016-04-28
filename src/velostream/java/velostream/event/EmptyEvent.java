package velostream.event;

import velostream.interfaces.IEvent;

public final class EmptyEvent extends Event {

  @Override
  public boolean isAlive(int ttl) {
    return false;
  }

  public static final IEvent INSTANCE = new EmptyEvent();
}
