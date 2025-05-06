package com.sleeved.looter.common.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LooterScrapingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public LooterScrapingException(String message) {
    super(message);
  }

  public LooterScrapingException(String message, Throwable cause) {
    super(message, cause);
  }

  public LooterScrapingException(Throwable cause) {
    super(cause);
  }
}
