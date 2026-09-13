package by.mashnyuk.hotels.model;

import by.mashnyuk.hotels.exceptions.IllegalArgumentCustomException;
import lombok.Getter;

@Getter
public enum HistogramParam {
    BRAND("brand"),
    CITY("city"),
    COUNTRY("country"),
    AMENITIES("amenities");

    private final String value;

    HistogramParam(String value) {
        this.value = value;
    }
    public static HistogramParam fromString(String param) {
        for (HistogramParam p : HistogramParam.values()) {
            if (p.value.equalsIgnoreCase(param) || p.name().equalsIgnoreCase(param)) {
                return p;
            }
        }
        throw new IllegalArgumentCustomException("Unsupported histogram parameter: " + param);
    }
}