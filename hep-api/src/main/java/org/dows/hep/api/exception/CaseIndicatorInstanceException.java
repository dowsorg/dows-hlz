package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class CaseIndicatorInstanceException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public CaseIndicatorInstanceException(String msg) {
    super(msg);
  }

  public CaseIndicatorInstanceException(Integer code, String msg) {
    super(code, msg);
  }

  public CaseIndicatorInstanceException(Throwable throwable) {
    super(throwable);
  }

  public CaseIndicatorInstanceException(StatusCode statusCode) {
    super(statusCode);
  }

  public CaseIndicatorInstanceException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public CaseIndicatorInstanceException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public CaseIndicatorInstanceException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public CaseIndicatorInstanceException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
