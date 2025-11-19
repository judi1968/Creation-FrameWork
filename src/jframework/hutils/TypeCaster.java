package jframework.hutils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class TypeCaster {

    public static Object cast(String value, Class<?> type) throws Exception {
        if (value == null) return null;

        if (type == String.class) return value;

        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == double.class || type == Double.class) return Double.parseDouble(value);
        if (type == float.class || type == Float.class) return Float.parseFloat(value);
        if (type == long.class || type == Long.class) return Long.parseLong(value);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);

        if (type == LocalDate.class) return LocalDate.parse(value);
        if (type == LocalDateTime.class) return LocalDateTime.parse(value);

        if (type == Date.class) return java.sql.Date.valueOf(value);

        throw new Exception("Type non géré : " + type.getName());
    }
}
