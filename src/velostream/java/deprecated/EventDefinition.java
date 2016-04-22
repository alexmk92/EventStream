package deprecated;

import java.util.HashMap;
import java.util.Map;

/**
 * Event Definition that is used to define the meta-data of an Event
 *
 * @author Richard Durley
 *         Date: 10/12/13
 *         Time: 21:00
 */
public class EventDefinition {

    private final String name;
    private final String description;
    Map<String, Object> event_fields = new HashMap<>();

    private final Field[] fields;
    private final Object[] defaultValues;


    public EventDefinition(String name, String description, Field[] fields, Object[] defaultValues) {
        if (name == null || name.equals(""))
            throw new IllegalArgumentException("name is mandatory");
        if (defaultValues == null)
            throw new IllegalArgumentException("default values are mandatory");
        if (fields == null)
            throw new IllegalArgumentException("fields are mandatory");
        if (fields.length != defaultValues.length)
            throw new IllegalArgumentException("fields and default values of same length are mandatory");

        if (defaultValues != null)
            for (Object o : defaultValues) {
                if (o == null)
                    throw new IllegalArgumentException("default values cannot be null");
                else if (!(o instanceof Integer || o instanceof Long || o instanceof Float || o instanceof String || o instanceof Double)) {
                    throw new IllegalArgumentException(("default values must be primitives or Strings"));
                }

            }

        this.name = name;
        this.fields = fields;
        this.defaultValues = defaultValues;
        this.description = description;
    }



    public Object[] getDefaultValues() {
        return defaultValues;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Field[] getFields() {
        return this.fields;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{event_name:");
        str.append(this.name);
        str.append(",event_description:");
        str.append(this.description);
        str.append(",fields:{");
        for (int i = 0; i < (fields.length); i++) {
            str.append("field:{");
            str.append("name:");
            str.append(fields[i].getName());
            str.append("type:");
            str.append(defaultValues[i].getClass().getSimpleName());
            str.append(",defaultvalue:");
            str.append(defaultValues[i]);
            str.append("},");
        }
        str.deleteCharAt(str.length() - 1);
        str.append("}}}");
        return str.toString();
    }


    /**
     * Defines a Field and its metadata
     */
    public static final class Field {

        private final String name;
        private final int column;

        public int getColumn() {
            return column;
        }

        public Field(String name, int column) {
            this.name = name;
            this.column = column;
        }

        public String getName() {
            return name;
        }

    }
}
