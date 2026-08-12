package tech.kayys.erp.foundation.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a use case (application service).
 * Helps with architectural testing and documentation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseCase {
    
    /**
     * Description of the use case.
     */
    String value() default "";
}
