package velostream.event;

import velostream.interfaces.IEvent;

/**
 * Created by Admin on 15/09/2014.
 */
public class EventTuple {
    public IEvent event1;
    public IEvent event2;

    public EventTuple(IEvent event1, IEvent event2) {
        this.event1 = event1;
        this.event2 = event2;
    }

}
