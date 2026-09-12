package by.mashnyuk.hotels.exceptions;

public class HotelNotFoundException extends RuntimeException {

    public HotelNotFoundException() {
        super("Hotel not found");
    }

    public HotelNotFoundException(Long id) {
        super("Hotel not found with id: " + id);
    }

    public HotelNotFoundException(String message) {
        super(message);
    }

    public HotelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public HotelNotFoundException(Throwable cause) {
        super(cause);
    }
}