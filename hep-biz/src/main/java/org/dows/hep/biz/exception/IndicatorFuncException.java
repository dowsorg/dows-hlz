package org.dows.hep.biz.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorFuncException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorFuncException(String msg) {
    super(msg);
  }

  public IndicatorFuncException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorFuncException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorFuncException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorFuncException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorFuncException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorFuncException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorFuncException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
