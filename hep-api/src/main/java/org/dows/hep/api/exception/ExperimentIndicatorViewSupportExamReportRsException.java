package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class ExperimentIndicatorViewSupportExamReportRsException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public ExperimentIndicatorViewSupportExamReportRsException(String msg) {
    super(msg);
  }

  public ExperimentIndicatorViewSupportExamReportRsException(Integer code, String msg) {
    super(code, msg);
  }

  public ExperimentIndicatorViewSupportExamReportRsException(Throwable throwable) {
    super(throwable);
  }

  public ExperimentIndicatorViewSupportExamReportRsException(StatusCode statusCode) {
    super(statusCode);
  }

  public ExperimentIndicatorViewSupportExamReportRsException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public ExperimentIndicatorViewSupportExamReportRsException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public ExperimentIndicatorViewSupportExamReportRsException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public ExperimentIndicatorViewSupportExamReportRsException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
