package ispw.project.project_ispw.exception;

public class ExceptionApplicationController extends Exception {

  public ExceptionApplicationController(String message) {
    super(message);
  }

  public ExceptionApplicationController(String message, Throwable cause) {
    super(message, cause);
  }

  public ExceptionApplicationController(Throwable cause) {
    super(cause);
  }
}