package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorExpressionItemRefException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorExpressionItemRefException(String msg) {
    super(msg);
  }

  public IndicatorExpressionItemRefException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorExpressionItemRefException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorExpressionItemRefException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorExpressionItemRefException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorExpressionItemRefException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorExpressionItemRefException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorExpressionItemRefException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
