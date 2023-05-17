package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class RiskDangerPointException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public RiskDangerPointException(String msg) {
    super(msg);
  }

  public RiskDangerPointException(Integer code, String msg) {
    super(code, msg);
  }

  public RiskDangerPointException(Throwable throwable) {
    super(throwable);
  }

  public RiskDangerPointException(StatusCode statusCode) {
    super(statusCode);
  }

  public RiskDangerPointException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public RiskDangerPointException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public RiskDangerPointException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public RiskDangerPointException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
