package it.alessandrohan.pollsystem.web.exception;

//400 bad request for duplicates
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
