package serverFacade;

public class Result<T> {
    private final boolean isSuccess;
    private final T data;
    private final String errorMessage;

    private Result(boolean isSuccess, T data, String errorMessage) {
        this.isSuccess = isSuccess;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, null);
    }

    // Overloaded success method for operations that don't return data
    public static Result<Void> success() {
        return new Result<>(true, null, null);
    }

    public static <T> Result<T> failure(String errorMessage) {
        return new Result<T>(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public T getData() {
        if (!isSuccess) {
            throw new IllegalStateException("Cannot get data from a failed result");
        }
        return data;
    }

    public String getErrorMessage() {
        if (isSuccess) {
            throw new IllegalStateException("Cannot get error message from a successful result");
        }
        return errorMessage;
    }
}
