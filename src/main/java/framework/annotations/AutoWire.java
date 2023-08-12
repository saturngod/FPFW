package framework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import java.lang.annotation.ElementType;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD , ElementType.METHOD})
public @interface AutoWire {
}
