package no.uib.inf219.api.annontation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExampleDoc {

    /**
     * @return Explanations of what the provided examples does
     */
    String[] exampleDesc() default "";

    /**
     * All the given examples must be valid
     *
     * @return examples for this part
     */
    String[] example();

    /**
     * @return If the examples might fail even if it is technically correct
     */
    boolean[] validateOnly() default false;
}
