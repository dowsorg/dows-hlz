package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorViewBaseInfoDescrRefException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorViewBaseInfoDescrRefException(String msg) {
    super(msg);
  }

  public IndicatorViewBaseInfoDescrRefException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorViewBaseInfoDescrRefException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorViewBaseInfoDescrRefException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorViewBaseInfoDescrRefException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorViewBaseInfoDescrRefException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorViewBaseInfoDescrRefException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorViewBaseInfoDescrRefException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
