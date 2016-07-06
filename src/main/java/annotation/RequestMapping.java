package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jojoldu@gmail.com on 2016-07-06.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //can use in method only.
public @interface RequestMapping {
    String value();
}
