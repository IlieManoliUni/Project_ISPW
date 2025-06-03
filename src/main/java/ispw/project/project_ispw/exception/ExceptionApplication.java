package ispw.project.project_ispw.exception;

public class ExceptionApplication extends Exception {

  public ExceptionApplication(String message) {
    super(message);
  }

  public ExceptionApplication(String message, Throwable cause) {
    super(message, cause);
  }

  public ExceptionApplication(Throwable cause) {
    super(cause);
  }
}