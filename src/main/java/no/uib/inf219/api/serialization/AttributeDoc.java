package no.uib.inf219.api.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeDoc {

    /**
     * @return If this is an required attribute
     */
    boolean required();

    /**
     * @return The path (ie key) to this attribute. If empty
     * {@link no.kh498.valentineRealms.core.parts.loader.ConfigLoader#classPath(Class)}
     * on the given class is used
     */
    String path() default "";

    /**
     * @return A remark on this attribute, essentially extra info given
     */
    String note() default "";

    /**
     * @return Specify what kind of value is to be expected. Useful if generics is used (f.eks a list {@code
     * List<String>} only be {@code List} in the documentation. Default value is the {@link Class#getSimpleName()} of
     * the field class
     */
    String type() default "";

    /**
     * If {@link #required()} is {@code true} setting this to anything else will show a warning and be ignored
     * <p>
     * If this is not modified, the attribute is not required and the attribute type is a primitive the <a
     * href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">java default value</a> for the
     * given primitive will be displayed as the default value.
     * <p>
     * This means that if this attribute is f.eks a {@code
     * boolean} the default value will be {@code false}
     *
     * @return The default value of this attribute
     */
    String defaultValue() default "";
}
