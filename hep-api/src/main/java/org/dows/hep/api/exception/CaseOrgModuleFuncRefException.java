package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class CaseOrgModuleFuncRefException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public CaseOrgModuleFuncRefException(String msg) {
    super(msg);
  }

  public CaseOrgModuleFuncRefException(Integer code, String msg) {
    super(code, msg);
  }

  public CaseOrgModuleFuncRefException(Throwable throwable) {
    super(throwable);
  }

  public CaseOrgModuleFuncRefException(StatusCode statusCode) {
    super(statusCode);
  }

  public CaseOrgModuleFuncRefException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public CaseOrgModuleFuncRefException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public CaseOrgModuleFuncRefException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public CaseOrgModuleFuncRefException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
