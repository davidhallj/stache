package io.github.davidhallj.stache.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionHelper {

    public static List<Field> findFieldsByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredFields()).
                filter(field -> field.isAnnotationPresent(annotationClass)).collect(Collectors.toList());
    }

    public static Map<String,Field> getFieldMapByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return findFieldsByAnnotation(clazz, annotationClass).stream()
                .collect(Collectors.toMap(
                        Field::getName,
                        Function.identity())
                );
    }

    public static Field getField(String fieldName, Class<?> clazz) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    //public static <T> void updateProperty(Object obj, Field field, T updateValue) {
    //
    //    if (updateValue != null && (field == null || !field.getType().isAssignableFrom(updateValue.getClass()))) {
    //        throw new IllegalArgumentException();
    //    }
    //
    //    callWriteMethod(obj, field, updateValue);
    //}
    //
    //public static <T> void updateProperty(Object obj, String fieldName, T updateValue) {
    //    try {
    //        Field field = obj.getClass().getDeclaredField(fieldName);
    //        //updateProperty(obj, field, updateValue);
    //        setField(obj, field, updateValue);
    //    } catch (Exception e) {
    //        throw new IllegalArgumentException(e);
    //    }
    //}
    //
    //private static <T> void callWriteMethod(Object obj, Field field, T updateValue) {
    //    try {
    //        new PropertyDescriptor(field.getName(), obj.getClass()).getWriteMethod().invoke(obj, updateValue);
    //    } catch (Exception e) {
    //        throw new IllegalArgumentException(e);
    //    }
    //}


    public static void setField(Object object, String fieldName, Object value) {
        try {
            var field = object.getClass().getDeclaredField(fieldName);
            setField(object, field, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to set " + fieldName + " of object", e);
        }
    }

    public static void setField(Object object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set " + field.getName() + " of object", e);
        }
    }



    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T getFieldValue(String fieldName, Object o) {
        final Method readerMethod = getGetter(fieldName, o.getClass());
        try {
            return (T) readerMethod.invoke(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static <T> T getFieldValue(Field field, Object o) {
        final String fieldName = field.getName();
        return getFieldValue(fieldName, o);
    }

    @SuppressWarnings("unchecked")
    public static <T> T extractFieldValue(Field field, Object o) {
        return (T) extractFieldValue(field, o, field.getType());
    }

    public static <T> T extractFieldValue(Field field, Object o, Class<T> fieldType) {
        try {
            field.setAccessible(true);
            return fieldType.cast(field.get(o));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getGetter(String fieldName, Class<?> clazz) {
        try {
            return new PropertyDescriptor(fieldName, clazz).getReadMethod();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
