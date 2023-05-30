package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorExpressionException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorExpressionException(String msg) {
    super(msg);
  }

  public IndicatorExpressionException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorExpressionException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorExpressionException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorExpressionException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorExpressionException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorExpressionException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorExpressionException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
