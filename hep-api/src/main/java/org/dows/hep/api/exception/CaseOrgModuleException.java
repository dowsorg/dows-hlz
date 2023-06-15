package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class CaseOrgModuleException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public CaseOrgModuleException(String msg) {
    super(msg);
  }

  public CaseOrgModuleException(Integer code, String msg) {
    super(code, msg);
  }

  public CaseOrgModuleException(Throwable throwable) {
    super(throwable);
  }

  public CaseOrgModuleException(StatusCode statusCode) {
    super(statusCode);
  }

  public CaseOrgModuleException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public CaseOrgModuleException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public CaseOrgModuleException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public CaseOrgModuleException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
