package ispw.project.project_ispw.exception;

public class CsvDaoException extends RuntimeException {

    public CsvDaoException(String message) {
        super(message);
    }

    public CsvDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvDaoException(Throwable cause) {
        super(cause);
    }
}