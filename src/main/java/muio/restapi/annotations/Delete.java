package muio.restapi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod(HttpMethod.DELETE)
public @interface Delete {
    String value();
    boolean regex() default false;
    int priority() default 0;
    String consumes();
    String produces();
}
