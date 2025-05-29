package ispw.project.project_ispw.exception;

public class ExceptionTmdbApi extends RuntimeException {

    public ExceptionTmdbApi(String message) {
        super(message);
    }

    public ExceptionTmdbApi(String message, Throwable cause) {
        super(message, cause);
    }
}

