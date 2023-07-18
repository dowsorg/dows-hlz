package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class CaseIndicatorExpressionException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public CaseIndicatorExpressionException(String msg) {
    super(msg);
  }

  public CaseIndicatorExpressionException(Integer code, String msg) {
    super(code, msg);
  }

  public CaseIndicatorExpressionException(Throwable throwable) {
    super(throwable);
  }

  public CaseIndicatorExpressionException(StatusCode statusCode) {
    super(statusCode);
  }

  public CaseIndicatorExpressionException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public CaseIndicatorExpressionException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public CaseIndicatorExpressionException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public CaseIndicatorExpressionException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
