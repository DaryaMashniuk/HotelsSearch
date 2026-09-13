package by.mashnyuk.hotels.exceptions;

public class HotelAlreadyExistsException extends RuntimeException {

    public HotelAlreadyExistsException(String name) {
        super(String.format("Hotel with name '%s' already exists", name));
    }
}