package ispw.project.project_ispw.exception;

public class ExceptionDao extends Exception {
  public ExceptionDao(String message, Throwable cause) {
    super(message, cause);
  }

  public ExceptionDao(String message) {
    super(message);
  }
}