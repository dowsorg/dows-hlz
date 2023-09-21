package org.dows.hep.config.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.exceptions.BaseException;
import org.dows.framework.api.i18n.UnifiedMessageSource;
import org.dows.framework.api.status.CommonStatusCode;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.util.StringUtils.hasText;

/**
 * @author runsix
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Slf4j
public class GlobalExceptionAdvice {
  @Value("${spring.application.name:}")
  private String serviceName;

  @Value("${spring.profiles.active:}")
  private String profile;

  @Autowired
  private UnifiedMessageSource unifiedMessageSource;

  @ExceptionHandler(value = BaseException.class)
  public Response<?> handleBaseException(HttpServletRequest request, HttpServletResponse response, BaseException e) {
    log.error("调用={}服务出现自定义异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
        request.getMethod(), e.getMessage(), e);//业务异常不打印堆栈 by wuzl
    if (e.getStatusCode() != null) {
      return Response.failed(e.getStatusCode());
    }
    return Response.failed(CommonStatusCode.FAILED.getCode(),getMessage(e));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Response<?> exception(MethodArgumentNotValidException e,HttpServletRequest request, HttpServletResponse response) {
    log.error("调用={}服务出现自定义异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
            request.getMethod(), e.getMessage(), e);
    return Response.failed(CommonStatusCode.FAILED.getCode(),getErrorMsg(e.getBindingResult(),false));
  }

  @ExceptionHandler(BindException.class)
  public Response<?> exception(BindException e,HttpServletRequest request, HttpServletResponse response) {
    log.error("调用={}服务出现自定义异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
            request.getMethod(), e.getMessage(), e);
    return Response.failed(CommonStatusCode.FAILED.getCode(),getErrorMsg(e.getBindingResult(),false));
  }
  private String getErrorMsg(BindingResult bindingResult,boolean containsAllError){
    if(null==bindingResult){
      return "";
    }
    StringBuilder sb=containsAllError?new StringBuilder():null;
    for(ObjectError item: bindingResult.getAllErrors()){
      if(StringUtils.hasLength(item.getDefaultMessage())){
        if(!containsAllError){
          return item.getDefaultMessage();
        }
        if(sb.length()>0){
          sb.append(", ");
        }
        if(item instanceof FieldError){
          sb.append(((FieldError) item).getField()).append(":");
        }
        if(StringUtils.hasLength(item.getDefaultMessage())){
          sb.append(item.getDefaultMessage());
        }else{
          sb.append("参数不正确");
        }
      }
    }
    return sb.toString();

  }


  @ExceptionHandler(value = Exception.class)
  public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
    if(ShareUtil.XObject.notEmpty(e.getCause() )
            &&e.getCause() instanceof BaseException){
        return handleBaseException(request, response, (BaseException) e);
    }
    log.error("抛出了Exception异常，调用={}服务出现自定义异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
        request.getMethod(), e.getMessage(), e);
    /*if (e.getStatusCode() != null) {
      return Response.failed(e.getStatusCode());
    }*/
    return Response.failed(CommonStatusCode.FAILED.getCode(),e.getMessage());
  }

  /**
   * 获取国际化消息
   *
   * @param e 异常
   * @return 国际化消息
   */
  public String getMessage(BaseException e) {
    String message = "";
    if (null != e.getStatusCode()) {
      String code = "response." + e.getStatusCode().toString();
      message = unifiedMessageSource.getMessage(code, e.getArgs());
    }
    if (!hasText(message)) {
      return e.getMessage();
    }
    return message;
  }
}
