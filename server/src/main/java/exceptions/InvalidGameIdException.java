package exceptions;

public class InvalidGameIdException extends Exception {
    public InvalidGameIdException(String message) {
        super(message);
    }
}
