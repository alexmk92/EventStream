package velostream.event;

import velostream.interfaces.IEvent;
import velostream.interfaces.IEventWorker;

/**
 * Created by Admin on 06/09/2014.
 */
public class PassthroughEventWorker implements IEventWorker {
    public IEvent work(IEvent eventIn) {
        return eventIn;
    }
}
