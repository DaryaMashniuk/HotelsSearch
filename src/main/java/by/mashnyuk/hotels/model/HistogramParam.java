package by.mashnyuk.hotels.model;

import by.mashnyuk.hotels.exceptions.IllegalArgumentCustomException;

public enum HistogramParam {
    BRAND,
    CITY,
    COUNTRY,
    AMENITIES;

    public static HistogramParam fromString(String value) {
        for (HistogramParam param : values()) {
            if (param.name().equalsIgnoreCase(value)) {
                return param;
            }
        }
        throw new IllegalArgumentCustomException("Unsupported histogram parameter: " + value);
    }
}