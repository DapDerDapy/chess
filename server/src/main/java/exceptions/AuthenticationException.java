package exceptions;

public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }

    // Optionally, if you want to include the cause of the exception
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
