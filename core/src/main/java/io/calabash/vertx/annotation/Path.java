package io.calabash.vertx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    HttpMethod[] httpMethods() default {};
    String value() default "";
    boolean regex() default false;
    int priority() default 0;
    String consumes() default "";
    String produces() default "";
}
