/*
 * Execute.java
 *
 * Author Richard Durley
 */
 
package velostream.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Wraps singleton methods arround Executors created cached thread pools
 * @author Richard Durley
 */
public class Execute {
    
    /** Creates a new instance of ThreadPool */
    public Execute() {
    }
    
    private static ExecutorService exec;

    /**
     * Gets an instance of ExecutorService for the application JETI threadpool
     * @return
     */
    public static synchronized ExecutorService getInstance()
    {
        if (exec==null)
            exec = Executors.newCachedThreadPool();
        
        return exec;
    }


    /**
     * Gets an instance of ExecutorService for the the given repository threadpool
     * @param databasename
     * @return
     */
    protected static synchronized ExecutorService getInstance(String databasename)
    {
        return getInstance();
    }

}
