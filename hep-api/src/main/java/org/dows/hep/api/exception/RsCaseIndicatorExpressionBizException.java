package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class RsCaseIndicatorExpressionBizException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public RsCaseIndicatorExpressionBizException(String msg) {
    super(msg);
  }

  public RsCaseIndicatorExpressionBizException(Integer code, String msg) {
    super(code, msg);
  }

  public RsCaseIndicatorExpressionBizException(Throwable throwable) {
    super(throwable);
  }

  public RsCaseIndicatorExpressionBizException(StatusCode statusCode) {
    super(statusCode);
  }

  public RsCaseIndicatorExpressionBizException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public RsCaseIndicatorExpressionBizException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public RsCaseIndicatorExpressionBizException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public RsCaseIndicatorExpressionBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
