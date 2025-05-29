package ispw.project.project_ispw.exception;

public class ExceptionAniListApi extends Exception {
    private final int httpStatusCode;
    private final String apiErrorMessage;

    public ExceptionAniListApi(String message) {
        super(message);
        this.httpStatusCode = -1;
        this.apiErrorMessage = null;
    }

    public ExceptionAniListApi(String message, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = -1;
        this.apiErrorMessage = null;
    }

    public ExceptionAniListApi(String message, int httpStatusCode, String apiErrorMessage) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.apiErrorMessage = apiErrorMessage;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getApiErrorMessage() {
        return apiErrorMessage;
    }
}