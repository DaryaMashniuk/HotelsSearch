package by.mashnyuk.hotels.exceptions;

public class IllegalArgumentCustomException extends RuntimeException {
    public IllegalArgumentCustomException(String message) {
        super(message);
    }
}