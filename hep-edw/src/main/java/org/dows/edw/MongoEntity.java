package org.dows.edw;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/9/12 19:50
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoEntity {
}
