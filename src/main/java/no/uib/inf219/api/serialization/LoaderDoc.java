package no.uib.inf219.api.serialization;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = LoaderDocs.class)
public @interface LoaderDoc {

    /**
     * @return The class of this attribute
     */
    Class<?> clazz();

    /**
     * @return The path (ie key) to this attribute. If not present
     * {@link no.kh498.valentineRealms.core.parts.loader.ConfigLoader#classPath(Class)}
     * on {@link #pathClass()} (or {@link #clazz()} if pathClass is not specified) is used.
     */
    String path() default "";

    /**
     * @return class to overload {@link #clazz()} in {@link #path()}
     */
    Class<?> pathClass() default Object.class;

    /**
     * @return A remark on this attribute, essentially extra info given
     */
    String note() default "";

    /**
     * @return Specify what kind of value is to be expected. Useful if generics is used (f.eks a list {@code
     * List<String>} only be {@code List} in the documentation. Default value is the {@link Class#getSimpleName()} of
     * {@link #clazz()}
     */
    String type() default "";
}
