package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorExpressionItemException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorExpressionItemException(String msg) {
    super(msg);
  }

  public IndicatorExpressionItemException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorExpressionItemException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorExpressionItemException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorExpressionItemException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorExpressionItemException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorExpressionItemException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorExpressionItemException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
