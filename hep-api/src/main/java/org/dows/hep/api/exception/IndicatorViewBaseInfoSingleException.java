package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorViewBaseInfoSingleException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorViewBaseInfoSingleException(String msg) {
    super(msg);
  }

  public IndicatorViewBaseInfoSingleException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorViewBaseInfoSingleException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorViewBaseInfoSingleException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorViewBaseInfoSingleException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorViewBaseInfoSingleException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorViewBaseInfoSingleException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorViewBaseInfoSingleException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
