package com.brian.backloghelperservice.exception;

/** Service exception for general non-retryable errors. */
public class BacklogHelperServiceException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message Exception message.
   * @param cause Cause of exception.
   */
  public BacklogHelperServiceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message Exception message.
   */
  public BacklogHelperServiceException(final String message) {
    super(message);
  }
}
