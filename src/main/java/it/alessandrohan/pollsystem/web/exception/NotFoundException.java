package it.alessandrohan.pollsystem.web.exception;

//404 not found
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
