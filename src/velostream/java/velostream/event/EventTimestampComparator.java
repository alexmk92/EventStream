package velostream.event;

import velostream.interfaces.IEvent;

import java.util.Comparator;

/**
 * Created by Admin on 09/09/2014.
 */
public class EventTimestampComparator implements Comparator<IEvent> {
    @Override
    public int compare(IEvent o1, IEvent o2) {
        if (o1 == o2) return 0;
        if (o1.getId()== o2.getId()) return 0;
        else if (o1.getTimestamp() > o2.getTimestamp())
            return 1;
        else if (o1.getTimestamp() < o2.getTimestamp()) return -1;
        else if (o1.getTimestamp() == o2.getTimestamp() && o1.getId()==(o2.getId())) return 0;
        else if (o1.getId()> o2.getId()) return 1;
        else return -1;
    }
}
