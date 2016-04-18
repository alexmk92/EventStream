package velostream.util;

/**
 * Created by Admin on 20/09/2014.
 */
public class ID {
    private static long id =0;

   public static synchronized long next() {
          return id++;
   }
}
