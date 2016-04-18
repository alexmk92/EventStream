package velostream.interfaces;

import velostream.event.EventTuple;

/**
 * Created by Admin on 15/09/2014.
 */
public interface IJoin {
    public EventTuple joinWhen(IEvent in1, IEvent in2);
}
