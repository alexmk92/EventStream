package velostream.event;

import velostream.interfaces.IEvent;

import java.util.Comparator;

/**
 * Created by Admin on 09/09/2014.
 */
public class EventIDComparator implements Comparator<IEvent> {
    @Override
    public int compare(IEvent o1, IEvent o2) {
        if (o1 == o2) return 0;
        else if (o1.getEventID() == o2.getEventID()) return 0;
        else if (o1.getEventID() > o2.getEventID()) return 1;
        else if (o1.getEventID() < o2.getEventID()) return -1;
        else return -1;
    }
}
