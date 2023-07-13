package org.dows.hep.biz;

import jakarta.servlet.http.HttpServletRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/13 11:31
 */
@ControllerAdvice
public class HeaderArgumentAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return setAppId(body);
    }


    private Object setAppId(Object body){
        HttpServletRequest req=getHttpRequest();
        if(ShareUtil.XObject.anyEmpty(body,req)){
            return body;
        }
        String appId= req.getHeader("Appid");
        if(ShareUtil.XObject.isEmpty(appId)){
            return body;
        }
        Object target=body;
        if(body instanceof List){
            List<?> list=(List<?>) body;
            if(ShareUtil.XObject.anyEmpty(list,()->list.get(0))){
                return body;
            }
            target=list.get(0);
        }
        Field appIdField= ReflectionUtils.findField(target.getClass(), "appId", String.class);
        if(null==appIdField){
            return body;
        }
        appIdField.setAccessible(true);
        ReflectionUtils.setField(appIdField,target,appId);
        return body;
    }

    static HttpServletRequest getHttpRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes != null ? requestAttributes.getRequest() : null;
    }


}
