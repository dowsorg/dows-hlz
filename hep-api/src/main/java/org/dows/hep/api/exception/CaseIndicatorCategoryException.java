package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class CaseIndicatorCategoryException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public CaseIndicatorCategoryException(String msg) {
    super(msg);
  }

  public CaseIndicatorCategoryException(Integer code, String msg) {
    super(code, msg);
  }

  public CaseIndicatorCategoryException(Throwable throwable) {
    super(throwable);
  }

  public CaseIndicatorCategoryException(StatusCode statusCode) {
    super(statusCode);
  }

  public CaseIndicatorCategoryException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public CaseIndicatorCategoryException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public CaseIndicatorCategoryException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public CaseIndicatorCategoryException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
