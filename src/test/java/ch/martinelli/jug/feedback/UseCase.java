package ch.martinelli.jug.feedback;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UseCase {
    String id();
    String scenario() default "Main Success Scenario";
    String[] businessRules() default {};
}
