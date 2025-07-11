package com.sleeved.looter.common.exception;

public class LooterSchedulerException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public LooterSchedulerException(String message) {
    super(message);
  }

  public LooterSchedulerException(String message, Throwable cause) {
    super(message, cause);
  }

  public LooterSchedulerException(Throwable cause) {
    super(cause);
  }

}
