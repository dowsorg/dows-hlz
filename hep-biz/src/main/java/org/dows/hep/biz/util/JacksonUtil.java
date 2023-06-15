package org.dows.hep.biz.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @author : wuzl
 * @date : 2023/4/22 18:36
 */
public class JacksonUtil {
    private final static ObjectMapper s_commonMapper;
    private final static ObjectMapper s_onlyFieldsMapper;
    static {
        final String dtFormat = "yyyy-MM-dd HH:mm:ss";
        s_commonMapper=createCommonObjectMapper(dtFormat);
        s_onlyFieldsMapper=createOnlyFieldsMapper(dtFormat);
    }

    protected JacksonUtil(){
    }

    public static ObjectMapper getCommonObjectMapper(){
        return s_commonMapper;
    }
    public static ObjectMapper getOnlyFieldsMapper(){ return s_onlyFieldsMapper; }

    public static ObjectMapper createCommonObjectMapper(String dateFormat) {
        return Jackson2ObjectMapperBuilder.json().build()
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setDateFormat(new SimpleDateFormat(dateFormat));
    }
    public static ObjectMapper createOnlyFieldsMapper(String dateFormat) {
        return createCommonObjectMapper(dateFormat)
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    }


    //reigon facade
    public static String toJson(Object obj,boolean onlyFields) throws JsonProcessingException {
        if (Objects.isNull(obj)) {
            return null;
        }
        return (onlyFields?s_onlyFieldsMapper:s_commonMapper).writeValueAsString(obj);
    }
    public static <T> T fromJson(String json, Class<T> clz) throws JsonProcessingException {
        if(ObjectUtils.isEmpty(json)){
            return null;
        }
        return s_commonMapper.readValue(json, clz);
    }
    public static <T> T fromJson(String json, TypeReference<T> type) throws JsonProcessingException {
        if(ObjectUtils.isEmpty(json)){
            return null;
        }
        return s_commonMapper.readValue(json, type);
    }

    public static <T> T deepCopy(Object obj,boolean onlyFields, Class<T> clz) throws JsonProcessingException{
        if(ObjectUtils.isEmpty(obj)){
            return null;
        }
        return fromJson(toJson(obj,onlyFields),clz);
    }
    public static <T> T deepCopy(Object obj,boolean onlyFields, TypeReference<T> type) throws JsonProcessingException {
        if(ObjectUtils.isEmpty(obj)){
            return null;
        }
        return fromJson(toJson(obj, onlyFields), type);
    }
    //endreigon
}
