package muio.restapi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Get {
    String value();
    boolean blocking() default false;
    boolean regex() default false;
    int priority() default 0;
    String consumes();
    String produces();
}
