package it.alessandrohan.pollsystem.web.exception;

//400 bad request
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
