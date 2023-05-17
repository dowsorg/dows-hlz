package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class RiskDeathModelException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public RiskDeathModelException(String msg) {
    super(msg);
  }

  public RiskDeathModelException(Integer code, String msg) {
    super(code, msg);
  }

  public RiskDeathModelException(Throwable throwable) {
    super(throwable);
  }

  public RiskDeathModelException(StatusCode statusCode) {
    super(statusCode);
  }

  public RiskDeathModelException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public RiskDeathModelException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public RiskDeathModelException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public RiskDeathModelException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
