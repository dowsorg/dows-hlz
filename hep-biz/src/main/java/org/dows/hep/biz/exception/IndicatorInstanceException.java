package org.dows.hep.biz.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorInstanceException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorInstanceException(String msg) {
    super(msg);
  }

  public IndicatorInstanceException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorInstanceException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorInstanceException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorInstanceException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorInstanceException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorInstanceException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorInstanceException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
