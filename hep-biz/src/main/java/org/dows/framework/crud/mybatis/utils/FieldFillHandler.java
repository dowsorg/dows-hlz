package org.dows.framework.crud.mybatis.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.beans.Introspector;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/9/25 10:39
 */
@Slf4j
public class FieldFillHandler implements MetaObjectHandler {
    private final Map<String, Class<?>> fieldTypMap = new HashMap<>();

    @PostConstruct
    public void init() {
        fieldTypMap.put("deleted", Boolean.class);
        fieldTypMap.put("dt", Date.class);
        fieldTypMap.put("updateDt", Date.class);
        fieldTypMap.put("name", String.class);
        fieldTypMap.put("label", String.class);
        fieldTypMap.put("title", String.class);
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        String snowflakeIdName = Introspector.decapitalize((metaObject.getOriginalObject().getClass().getSimpleName() + "Id")).replace("Instance", "").replace("Entity", "");
        if (metaObject.hasSetter(snowflakeIdName)){
            Object o = metaObject.getValue(snowflakeIdName);
            if (o == null ) {
                fillStrategy(metaObject, snowflakeIdName, IdWorker.getIdStr());
            }
        }
        fillField(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillField(metaObject);
    }

    private void fillField(MetaObject metaObject){
        fieldTypMap.forEach((k, v) ->fillField(metaObject, k,v));
    }

    private void fillField(MetaObject metaObject, String k, Class<?> v){
        String fieldLetter = k + "Code";
        if (metaObject.hasSetter(k)) {
            Object o = metaObject.getValue(k);
            if (o == null && v.getName().equals("java.lang.Boolean")) {
                fillStrategy(metaObject, k, false);
            } else if (o == null && v.getName().equals("java.util.Date")) {
                fillStrategy(metaObject, k, new Date());
            }
        } else if (metaObject.hasSetter(fieldLetter)) {
            Object o = metaObject.getValue(fieldLetter);
            if (o != null && v.getName().equals("java.lang.String") && !o.toString().equals("")) {
                try {
                    setFieldValByName(fieldLetter, PinyinHelper.getShortPinyin(o.toString()), metaObject);
                } catch (PinyinException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }


}
