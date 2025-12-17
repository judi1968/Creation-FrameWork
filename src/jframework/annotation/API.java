package jframework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method; 

// =================== ANNOTATIONS ===================

// Annotation pour la table
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface API {
    FormatApi format() default FormatApi.SIMPLE;
}