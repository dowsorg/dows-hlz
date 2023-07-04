package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class ExperimentIndicatorViewMonitorFollowupReportRsException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public ExperimentIndicatorViewMonitorFollowupReportRsException(String msg) {
    super(msg);
  }

  public ExperimentIndicatorViewMonitorFollowupReportRsException(Integer code, String msg) {
    super(code, msg);
  }

  public ExperimentIndicatorViewMonitorFollowupReportRsException(Throwable throwable) {
    super(throwable);
  }

  public ExperimentIndicatorViewMonitorFollowupReportRsException(StatusCode statusCode) {
    super(statusCode);
  }

  public ExperimentIndicatorViewMonitorFollowupReportRsException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public ExperimentIndicatorViewMonitorFollowupReportRsException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public ExperimentIndicatorViewMonitorFollowupReportRsException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public ExperimentIndicatorViewMonitorFollowupReportRsException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
