package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorViewBaseInfoDescrException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorViewBaseInfoDescrException(String msg) {
    super(msg);
  }

  public IndicatorViewBaseInfoDescrException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorViewBaseInfoDescrException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorViewBaseInfoDescrException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorViewBaseInfoDescrException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorViewBaseInfoDescrException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorViewBaseInfoDescrException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorViewBaseInfoDescrException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
