package no.uib.inf219.api.annontation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description of an enum to be used by documentation system to document custom enums
 *
 * @author Elg
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValueDesc {

    String value();

    boolean isDefault() default false;
}
