package jframework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



// =================== ANNOTATIONS ===================

@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value() default "";
}