package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorJudgeHealthGuidanceException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorJudgeHealthGuidanceException(String msg) {
    super(msg);
  }

  public IndicatorJudgeHealthGuidanceException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorJudgeHealthGuidanceException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorJudgeHealthGuidanceException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorJudgeHealthGuidanceException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorJudgeHealthGuidanceException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorJudgeHealthGuidanceException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorJudgeHealthGuidanceException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
