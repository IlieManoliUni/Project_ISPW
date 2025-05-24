package ispw.project.project_ispw.exception;

public class ExceptionAniListApi extends Exception {
    private int httpStatusCode;
    private String apiErrorMessage;

    public ExceptionAniListApi(String message) {
        super(message);
    }

    public ExceptionAniListApi(String message, Throwable cause) {
        super(message, cause);
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

