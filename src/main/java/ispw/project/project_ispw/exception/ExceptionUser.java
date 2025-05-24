package ispw.project.project_ispw.exception;

public class ExceptionUser extends Exception {

    public ExceptionUser(String message) {
        super(message);
    }

    public ExceptionUser(String message, Throwable cause) {
        super(message, cause);
    }
}

