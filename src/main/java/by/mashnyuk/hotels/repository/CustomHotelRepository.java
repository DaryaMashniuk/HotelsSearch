package by.mashnyuk.hotels.repository;

import java.util.Map;

public interface CustomHotelRepository {
    Map<String, Long> getHistogramByAttribute(String param);
}