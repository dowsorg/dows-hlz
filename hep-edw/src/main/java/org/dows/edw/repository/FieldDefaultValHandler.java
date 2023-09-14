package org.dows.edw.repository;

import lombok.RequiredArgsConstructor;
import org.dows.edw.FieldFill;
import org.dows.edw.LogicDel;
import org.dows.edw.MongoEntity;
import org.dows.edw.MongoEntityId;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author fhb
 * @version 1.0
 * @description 字段默认值处理器
 * @date 2023/9/13 11:32
 **/
@Component
@RequiredArgsConstructor
public class FieldDefaultValHandler {

    private final IdGenerator idGenerator;

    public <T> T setDefaultValue(Object obj, Class<T> clazz) {
        boolean instance = clazz.isInstance(obj);
        if (instance) {
            T t = clazz.cast(obj);
            // 默认值
            if (!t.getClass().isAnnotationPresent(MongoEntity.class)) {
                return t;
            }
            Field[] fields = t.getClass().getDeclaredFields();
            if (fields.length == 0) {
                return t;
            }
            Arrays.stream(fields).forEach(field -> {
                boolean present = field.isAnnotationPresent(FieldFill.class);
                if (present) {

                    Class<?> fieldType = field.getType();
                    if (field.isAnnotationPresent(MongoEntityId.class)) { // 主键值
                        // Long 类型
                        if (fieldType.equals(Long.class)) {
                            field.setAccessible(true);
                            try {
                                field.set(t, idGenerator.nextId());
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        // String 类型
                        if (fieldType.equals(String.class)) {
                            field.setAccessible(true);
                            try {
                                field.set(t, idGenerator.nextIdStr());
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else if (field.isAnnotationPresent(LogicDel.class)) { // 逻辑删除
                        if (fieldType.equals(Integer.class)) {
                            field.setAccessible(true);
                            try {
                                field.set(t, 0);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (fieldType.equals(Boolean.class)) {
                            field.setAccessible(true);
                            try {
                                field.set(t, Boolean.FALSE);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else { // 普通字段
                        // Long 类型
                        if (fieldType.equals(LocalDateTime.class)) {
                            field.setAccessible(true);
                            try {
                                field.set(t, LocalDateTime.now());
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                }
            });
            return t;
        }
        return null;
    }
}
