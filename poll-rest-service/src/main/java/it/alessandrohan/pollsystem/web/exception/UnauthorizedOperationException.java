package it.alessandrohan.pollsystem.web.exception;

//401 unauthorized
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
