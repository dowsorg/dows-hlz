package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorViewMonitorFollowupException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorViewMonitorFollowupException(String msg) {
    super(msg);
  }

  public IndicatorViewMonitorFollowupException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorViewMonitorFollowupException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorViewMonitorFollowupException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorViewMonitorFollowupException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorViewMonitorFollowupException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorViewMonitorFollowupException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorViewMonitorFollowupException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
