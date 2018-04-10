package lombok;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nan.zhang on 18-2-10.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface HelloWorld {

    String builderMethodName() default "builder";

    /**
     * Name of the instance method in the builder class that creates an instance of your {@code @Builder}-annotated class.
     */
    String buildMethodName() default "build";

    /**
     * Name of the builder class.
     * Default for {@code @Builder} on types and constructors: {@code (TypeName)Builder}.
     * Default for {@code @Builder} on static methods: {@code (ReturnTypeName)Builder}.
     */
    String builderClassName() default "";

    /**
     * Normally the builder's 'set' methods are fluent, meaning, they have the same name as the field. Set this
     * to {@code false} to name the setter method for field {@code someField}: {@code setSomeField}.
     * <p>
     * <strong>Default: true</strong>
     */
    boolean fluent() default true;

    /**
     * Normally the builder's 'set' methods are chaining, meaning, they return the builder so that you can chain
     * calls to set methods. Set this to {@code false} to have these 'set' methods return {@code void} instead.
     * <p>
     * <strong>Default: true</strong>
     */
    boolean chain() default true;
}
