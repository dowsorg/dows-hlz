package org.dows.hep.api.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resubmit {
    boolean value() default false;

    /**
     * 默认-1 ，不限时间，单位时间为秒
     *
     * @return
     */
    long duration() default -1L;
}
