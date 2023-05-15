package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorJudgeHealthManagementGoalException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorJudgeHealthManagementGoalException(String msg) {
    super(msg);
  }

  public IndicatorJudgeHealthManagementGoalException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorJudgeHealthManagementGoalException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorJudgeHealthManagementGoalException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorJudgeHealthManagementGoalException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorJudgeHealthManagementGoalException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorJudgeHealthManagementGoalException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorJudgeHealthManagementGoalException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
