package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorJudgeRiskFactorException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorJudgeRiskFactorException(String msg) {
    super(msg);
  }

  public IndicatorJudgeRiskFactorException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorJudgeRiskFactorException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorJudgeRiskFactorException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorJudgeRiskFactorException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorJudgeRiskFactorException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorJudgeRiskFactorException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorJudgeRiskFactorException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
