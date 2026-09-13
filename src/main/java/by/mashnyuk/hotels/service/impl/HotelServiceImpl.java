package by.mashnyuk.hotels.service.impl;

import by.mashnyuk.hotels.exceptions.HotelNotFoundException;
import by.mashnyuk.hotels.model.Amenity;
import by.mashnyuk.hotels.model.Hotel;
import by.mashnyuk.hotels.model.dto.request.CreateHotelDto;
import by.mashnyuk.hotels.model.dto.request.HotelSearchCriteria;
import by.mashnyuk.hotels.model.dto.response.HotelFullDto;
import by.mashnyuk.hotels.model.dto.response.HotelShortDto;
import by.mashnyuk.hotels.repository.AmenityRepository;
import by.mashnyuk.hotels.service.HotelService;
import by.mashnyuk.hotels.mapper.HotelMapper;
import by.mashnyuk.hotels.repository.HotelRepository;
import by.mashnyuk.hotels.specifications.HotelSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    @Override
    public List<HotelShortDto> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotelMapper.toShortDtoList(hotels);
    }

    @Override
    public HotelFullDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        return hotelMapper.toFullDto(hotel);
    }

    @Override
    @Transactional
    public HotelShortDto createHotel(CreateHotelDto createHotelDto) {
        Hotel hotel = hotelMapper.toEntity(createHotelDto);
        Hotel savedHotel = hotelRepository.save(hotel);
        return hotelMapper.toShortDto(savedHotel);
    }

    @Override
    @Transactional
    public void addAmenitiesToHotel(Long id, List<String> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return;
        }

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        if (hotel.getAmenities() == null) {
            hotel.setAmenities(new HashSet<>());
        }

        for (String amenityName : amenities) {
            if (amenityName == null || amenityName.isBlank()) {
                continue;
            }
            Amenity amenity = amenityRepository.findByName(amenityName)
                    .orElseGet(() -> amenityRepository.save(
                            Amenity.builder()
                                    .name(amenityName.trim())
                                    .build()
                    ));
            hotel.getAmenities().add(amenity);
        }

        hotelRepository.save(hotel);
    }

    @Override
    public List<HotelShortDto> searchHotels(HotelSearchCriteria criteria) {
        Specification<Hotel> spec = HotelSpecification.build(criteria);
        return hotelRepository.findAll(spec).stream()
                .map(hotelMapper::toShortDto)
                .toList();
    }

}
