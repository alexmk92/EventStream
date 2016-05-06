package velostream.stream.workers;

import velostream.interfaces.IEvent;
import velostream.interfaces.IEventWorker;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 06/09/2014.
 */
public class PassthroughEventWorker implements IEventWorker {
    public List<IEvent> work(IEvent eventIn, Map<String, Object> params) {
        return Arrays.asList(eventIn);
    }
}
