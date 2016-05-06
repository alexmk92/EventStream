package velostream.interfaces;


import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 23/08/14
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public interface IEventWorker {
    public List<IEvent> work(IEvent toprocess, Map<String, Object> params);
}
