package jframework.hutils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

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

    public static boolean isComplexObject(Parameter parameter) {
        Class<?> type = parameter.getType();
        return isComplexObject(type)   ;     
    }

    public static boolean isComplexObject(Class<?> type){
        if (type.isPrimitive()) return false;

        if (type == String.class) return false;
        if (Number.class.isAssignableFrom(type)) return false;
        if (type == Boolean.class || type == Character.class) return false;

        if (java.util.Date.class.isAssignableFrom(type)) return false;
        if (java.time.temporal.Temporal.class.isAssignableFrom(type)) return false;

        if (Collection.class.isAssignableFrom(type)) return false;
        if (Map.class.isAssignableFrom(type)) return false;

        Package pkg = type.getPackage();
        if (pkg != null && pkg.getName().startsWith("java.")) return false;

        return true;
    }

    public static Object castObject(Parameter parameter, HttpServletRequest request) throws Exception {
        Class<?> type = parameter.getType();
        Object instanceParametre = type.getDeclaredConstructor().newInstance();
        String nameParameter = parameter.getName();
        return completedFieldParameter(instanceParametre, nameParameter, request); 
    }
    public static Object completedFieldParameter(Object instance, String nameObject, HttpServletRequest request) throws Exception{
        Class<?> classInstance = instance.getClass();
        Field[] fields = classInstance.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String nameParameter = nameObject+"."+fieldName;
            if (field.getType().isArray()) {
                String[] values = request.getParameterValues(nameParameter);
                field.set(instance, values);
                continue;
            }
            if (field.get(instance) == null) {
                String valueString = request.getParameter(nameParameter);
                field.set(instance, cast(valueString, field.getType()));
            }
            if (field.get(instance) == null) {
                if (isComplexObject(field.getType())) {
                    Object fieldObject = field.getType().getDeclaredConstructor().newInstance();
                    field.set(instance, completedFieldParameter(fieldObject, nameParameter, request)); 
                }
            }
        }
        return instance;
    }

}
