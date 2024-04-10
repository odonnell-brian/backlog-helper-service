package com.brian.backloghelperservice;

/** Service exception for not found errors. */
public class BacklogHelperServiceNotFoundException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message Exception message.
   * @param cause Cause of exception.
   */
  public BacklogHelperServiceNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message Exception message.
   */
  public BacklogHelperServiceNotFoundException(final String message) {
    super(message);
  }
}
