package muio.restapi.annotations;



import io.vertx.core.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    HttpMethod[] httpMethods() default { HttpMethod.GET, HttpMethod.POST };
    String value();
    boolean blocking() default false;
    boolean regex() default false;
    int priority() default 0;
    String consumes();
    String produces();
}
