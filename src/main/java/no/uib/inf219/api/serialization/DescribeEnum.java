package no.uib.inf219.api.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Elg
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DescribeEnum {

    /**
     * Describe the given enum with {@link EnumValueDesc} in the documentation
     *
     * @return The enums to describe
     */
    Class<? extends Enum<?>>[] value();
}
