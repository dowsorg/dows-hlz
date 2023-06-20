package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class ExperimentIndicatorViewBaseInfoRsException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public ExperimentIndicatorViewBaseInfoRsException(String msg) {
    super(msg);
  }

  public ExperimentIndicatorViewBaseInfoRsException(Integer code, String msg) {
    super(code, msg);
  }

  public ExperimentIndicatorViewBaseInfoRsException(Throwable throwable) {
    super(throwable);
  }

  public ExperimentIndicatorViewBaseInfoRsException(StatusCode statusCode) {
    super(statusCode);
  }

  public ExperimentIndicatorViewBaseInfoRsException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public ExperimentIndicatorViewBaseInfoRsException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public ExperimentIndicatorViewBaseInfoRsException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public ExperimentIndicatorViewBaseInfoRsException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
