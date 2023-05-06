package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorViewMonitorFollowupFollowupContentException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorViewMonitorFollowupFollowupContentException(String msg) {
    super(msg);
  }

  public IndicatorViewMonitorFollowupFollowupContentException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorViewMonitorFollowupFollowupContentException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorViewMonitorFollowupFollowupContentException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorViewMonitorFollowupFollowupContentException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorViewMonitorFollowupFollowupContentException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorViewMonitorFollowupFollowupContentException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorViewMonitorFollowupFollowupContentException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
