package velostream.util;

import velostream.interfaces.IEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Admin on 23/09/2014.
 */
public class FieldUtils {

    public static Field getField(IEvent event, String fieldname) {
        Field f = null;
        try {
            f = event.getClass().getField(fieldname);
        } catch (NoSuchFieldException e2) {
        } finally {
            return f;
        }
    }

    public static Object getFieldValueViaGetter(IEvent event, String fieldname) {
        Method f = null;
        Object toreturn = null;
        try {
            f = event.getClass().getMethod("get"+fieldname,null);
            toreturn = f.invoke(event, null);
        } catch (NoSuchMethodException e2) {
        }
        catch (IllegalAccessException e3) {
        }
        catch (InvocationTargetException e4) {
        }

        return toreturn;
    }


    public static double getDoubleFieldValue(IEvent e, Field f) {
        double o = 0;
        try {
            if (f!=null)
                o = (double) f.get(e);
        } catch (IllegalAccessException e1) {
        } catch (ClassCastException e2) {
            try {
                o = Double.parseDouble(f.get(e).toString());
            } catch (NumberFormatException e3) {
            }
        } finally {
            return o;
        }
    }

}
