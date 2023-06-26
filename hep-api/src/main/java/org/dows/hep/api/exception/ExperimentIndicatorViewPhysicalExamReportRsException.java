package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class ExperimentIndicatorViewPhysicalExamReportRsException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public ExperimentIndicatorViewPhysicalExamReportRsException(String msg) {
    super(msg);
  }

  public ExperimentIndicatorViewPhysicalExamReportRsException(Integer code, String msg) {
    super(code, msg);
  }

  public ExperimentIndicatorViewPhysicalExamReportRsException(Throwable throwable) {
    super(throwable);
  }

  public ExperimentIndicatorViewPhysicalExamReportRsException(StatusCode statusCode) {
    super(statusCode);
  }

  public ExperimentIndicatorViewPhysicalExamReportRsException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public ExperimentIndicatorViewPhysicalExamReportRsException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public ExperimentIndicatorViewPhysicalExamReportRsException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public ExperimentIndicatorViewPhysicalExamReportRsException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
