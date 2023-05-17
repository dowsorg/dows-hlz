package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class RiskModelException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public RiskModelException(String msg) {
    super(msg);
  }

  public RiskModelException(Integer code, String msg) {
    super(code, msg);
  }

  public RiskModelException(Throwable throwable) {
    super(throwable);
  }

  public RiskModelException(StatusCode statusCode) {
    super(statusCode);
  }

  public RiskModelException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public RiskModelException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public RiskModelException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public RiskModelException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
