package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorViewMonitorFollowupContentRefException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorViewMonitorFollowupContentRefException(String msg) {
    super(msg);
  }

  public IndicatorViewMonitorFollowupContentRefException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorViewMonitorFollowupContentRefException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorViewMonitorFollowupContentRefException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorViewMonitorFollowupContentRefException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorViewMonitorFollowupContentRefException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorViewMonitorFollowupContentRefException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorViewMonitorFollowupContentRefException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
