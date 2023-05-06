package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorViewBaseInfoMonitorException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorViewBaseInfoMonitorException(String msg) {
    super(msg);
  }

  public IndicatorViewBaseInfoMonitorException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorViewBaseInfoMonitorException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorViewBaseInfoMonitorException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorViewBaseInfoMonitorException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorViewBaseInfoMonitorException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorViewBaseInfoMonitorException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorViewBaseInfoMonitorException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
