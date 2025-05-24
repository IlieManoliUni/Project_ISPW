package ispw.project.project_ispw.exception;

/**
 * Custom exception class for signaling errors that occur within the application's
 * business logic layer (e.g., in the ApplicationController).
 *
 * This exception is designed to wrap lower-level exceptions (like DAO errors)
 * or to indicate specific business rule violations (e.g., "username already exists")
 * with messages that are more relevant to the application's user interface or flow.
 */
public class ExceptionApplicationController extends Exception {

  /**
   * Constructs a new ApplicationException with the specified detail message.
   * The cause is not initialized.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage() method).
   */
  public ExceptionApplicationController(String message) {
    super(message);
  }

  /**
   * Constructs a new ApplicationException with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause   The cause (which is saved for later retrieval by the getCause() method).
   * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public ExceptionApplicationController(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new ApplicationException with the specified cause and a detail message of
   * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
   * This constructor is useful for exceptions that are little more than wrappers for other throwables.
   *
   * @param cause The cause (which is saved for later retrieval by the getCause() method).
   */
  public ExceptionApplicationController(Throwable cause) {
    super(cause);
  }
}