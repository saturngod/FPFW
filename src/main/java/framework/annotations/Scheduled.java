package framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface Scheduled {
    public int fixedRate() default 0;
    public int delay() default 0;

    public String cron() default "";
}
