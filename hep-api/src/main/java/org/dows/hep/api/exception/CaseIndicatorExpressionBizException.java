package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class CaseIndicatorExpressionBizException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public CaseIndicatorExpressionBizException(String msg) {
    super(msg);
  }

  public CaseIndicatorExpressionBizException(Integer code, String msg) {
    super(code, msg);
  }

  public CaseIndicatorExpressionBizException(Throwable throwable) {
    super(throwable);
  }

  public CaseIndicatorExpressionBizException(StatusCode statusCode) {
    super(statusCode);
  }

  public CaseIndicatorExpressionBizException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public CaseIndicatorExpressionBizException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public CaseIndicatorExpressionBizException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public CaseIndicatorExpressionBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
