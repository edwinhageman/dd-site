package emh.dd_site;

import java.lang.reflect.Field;

public class RecursionUtils {

    private RecursionUtils() {
    }

    public static void setPrivateField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
