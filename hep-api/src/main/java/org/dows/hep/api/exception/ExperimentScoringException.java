package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class ExperimentScoringException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public ExperimentScoringException(String msg) {
    super(msg);
  }

  public ExperimentScoringException(Integer code, String msg) {
    super(code, msg);
  }

  public ExperimentScoringException(Throwable throwable) {
    super(throwable);
  }

  public ExperimentScoringException(StatusCode statusCode) {
    super(statusCode);
  }

  public ExperimentScoringException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public ExperimentScoringException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public ExperimentScoringException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public ExperimentScoringException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
