package by.mashnyuk.hotels.service;

import by.mashnyuk.hotels.model.dto.request.CreateHotelDto;
import by.mashnyuk.hotels.model.dto.response.HotelFullDto;
import by.mashnyuk.hotels.model.dto.response.HotelShortDto;

import java.util.List;

public interface HotelService {

    /**
     * Retrieves all hotels in short summary format.
     *
     * @return list of HotelShortDto
     */
    List<HotelShortDto> getAllHotels();

    /**
     * Retrieves detailed information about a hotel by its ID.
     *
     * @param id hotel identifier
     * @return HotelFullDto
     */
    HotelFullDto getHotelById(Long id);

    /**
     * Creates a new hotel record.
     *
     * @param createHotelDto payload with hotel details
     * @return HotelShortDto representation of created hotel
     */
    HotelShortDto createHotel(CreateHotelDto createHotelDto);

    /**
     * Adds a list of amenities to a specific hotel.
     *
     * @param id hotel identifier
     * @param amenities list of amenity names to attach
     */
    void addAmenitiesToHotel(Long id, List<String> amenities);
}
