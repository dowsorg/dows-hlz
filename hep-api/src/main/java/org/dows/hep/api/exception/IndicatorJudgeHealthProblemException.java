package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorJudgeHealthProblemException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorJudgeHealthProblemException(String msg) {
    super(msg);
  }

  public IndicatorJudgeHealthProblemException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorJudgeHealthProblemException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorJudgeHealthProblemException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorJudgeHealthProblemException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorJudgeHealthProblemException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorJudgeHealthProblemException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorJudgeHealthProblemException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
