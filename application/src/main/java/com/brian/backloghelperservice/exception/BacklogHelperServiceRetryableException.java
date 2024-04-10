package com.brian.backloghelperservice.exception;

/** Service exception for retryable errors. */
public class BacklogHelperServiceRetryableException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message Exception message.
   * @param cause Cause of exception.
   */
  public BacklogHelperServiceRetryableException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message Exception message.
   */
  public BacklogHelperServiceRetryableException(final String message) {
    super(message);
  }
}
