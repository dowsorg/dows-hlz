package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class ExperimentIndicatorViewPhysicalExamRsException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public ExperimentIndicatorViewPhysicalExamRsException(String msg) {
    super(msg);
  }

  public ExperimentIndicatorViewPhysicalExamRsException(Integer code, String msg) {
    super(code, msg);
  }

  public ExperimentIndicatorViewPhysicalExamRsException(Throwable throwable) {
    super(throwable);
  }

  public ExperimentIndicatorViewPhysicalExamRsException(StatusCode statusCode) {
    super(statusCode);
  }

  public ExperimentIndicatorViewPhysicalExamRsException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public ExperimentIndicatorViewPhysicalExamRsException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public ExperimentIndicatorViewPhysicalExamRsException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public ExperimentIndicatorViewPhysicalExamRsException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
