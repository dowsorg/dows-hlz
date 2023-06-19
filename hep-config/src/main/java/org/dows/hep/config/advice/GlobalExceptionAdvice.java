package org.dows.hep.config.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.exceptions.BaseException;
import org.dows.framework.api.i18n.UnifiedMessageSource;
import org.dows.framework.api.status.CommonStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
        request.getMethod(), e.getMessage(), e);
    if (e.getStatusCode() != null) {
      return Response.failed(e.getStatusCode());
    }
    return Response.failed(CommonStatusCode.FAILED.getCode(),getMessage(e));
  }

  @ExceptionHandler(value = Exception.class)
  public Response<?> handleException(HttpServletRequest request, HttpServletResponse response, BaseException e) {
    log.error("抛出了Exception异常，调用={}服务出现自定义异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
        request.getMethod(), e.getMessage(), e);
    if (e.getStatusCode() != null) {
      return Response.failed(e.getStatusCode());
    }
    return Response.failed(CommonStatusCode.FAILED.getCode(),getMessage(e));
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
