package ispw.project.project_ispw.exception;

public class ExceptionDatabase extends RuntimeException {

    public ExceptionDatabase(String message) {
        super(message);
    }

    public ExceptionDatabase(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionDatabase(Throwable cause) {
        super(cause);
    }
}