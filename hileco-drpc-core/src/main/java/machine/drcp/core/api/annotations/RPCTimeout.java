package machine.drcp.core.api.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Specifies the timeout of a callback window for a method, implementations should at least use this as an indication
 * on how long a callback window may be open, but don't have to exactly follow it.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface RPCTimeout {

    public int value();

    public TimeUnit unit() default TimeUnit.SECONDS;

}
