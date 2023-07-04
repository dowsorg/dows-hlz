package org.dows.hep.api.annotation;


import org.dows.hep.api.enums.EnumCalcCode;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CalcCode {
    EnumCalcCode code();
}
