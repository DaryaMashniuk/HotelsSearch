package by.mashnyuk.hotels.controller;


import by.mashnyuk.hotels.model.dto.request.CreateHotelDto;
import by.mashnyuk.hotels.model.dto.request.HotelSearchCriteria;
import by.mashnyuk.hotels.model.dto.response.HotelFullDto;
import by.mashnyuk.hotels.model.dto.response.HotelShortDto;
import by.mashnyuk.hotels.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/property-view")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/hotels")
    public ResponseEntity<List<HotelShortDto>> getHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelFullDto> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @PostMapping("/hotels")
    public ResponseEntity<HotelShortDto> createHotel(@Valid @RequestBody CreateHotelDto createHotelDto) {
        HotelShortDto createdHotel = hotelService.createHotel(createHotelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHotel);
    }

    @PostMapping("/hotels/{id}/amenities")
    @ResponseStatus(HttpStatus.OK)
    public void addAmenities(@PathVariable Long id, @RequestBody List<String> amenities) {
        hotelService.addAmenitiesToHotel(id, amenities);
    }

    @GetMapping("/search")
    public ResponseEntity<List<HotelShortDto>> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> amenities) {

        HotelSearchCriteria criteria = HotelSearchCriteria.builder()
                .name(name)
                .brand(brand)
                .city(city)
                .country(country)
                .amenities(amenities)
                .build();

        return ResponseEntity.ok(hotelService.searchHotels(criteria));
    }

}