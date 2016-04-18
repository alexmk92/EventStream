package velostream.interfaces;


/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 23/08/14
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public interface IEventWorker {
    public IEvent work(IEvent toprocess);
}
