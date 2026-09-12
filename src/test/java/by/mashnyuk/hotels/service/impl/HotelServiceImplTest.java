package by.mashnyuk.hotels.service.impl;

import by.mashnyuk.hotels.exceptions.HotelNotFoundException;
import by.mashnyuk.hotels.model.Amenity;
import by.mashnyuk.hotels.model.Hotel;
import by.mashnyuk.hotels.model.dto.CreateHotelDto;
import by.mashnyuk.hotels.model.dto.HotelFullDto;
import by.mashnyuk.hotels.model.dto.HotelShortDto;
import by.mashnyuk.hotels.repository.AmenityRepository;
import by.mashnyuk.hotels.mapper.HotelMapper;
import by.mashnyuk.hotels.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Hotel sampleHotel;
    private HotelShortDto sampleShortDto;
    private HotelFullDto sampleFullDto;

    @BeforeEach
    void setUp() {
        sampleHotel = Hotel.builder()
                .id(1L)
                .name("DoubleTree by Hilton")
                .brand("Hilton")
                .amenities(new HashSet<>())
                .build();

        sampleShortDto = HotelShortDto.builder()
                .id(1L)
                .name("DoubleTree by Hilton")
                .build();

        sampleFullDto = HotelFullDto.builder()
                .id(1L)
                .name("DoubleTree by Hilton")
                .brand("Hilton")
                .build();
    }

    @Nested
    @DisplayName("getAllHotels()")
    class GetAllHotelsTests {

        @Test
        @DisplayName("Should return list of short hotel DTOs")
        void shouldReturnListOfHotelShortDtos() {
            when(hotelRepository.findAll()).thenReturn(List.of(sampleHotel));
            when(hotelMapper.toShortDtoList(List.of(sampleHotel))).thenReturn(List.of(sampleShortDto));

            List<HotelShortDto> result = hotelService.getAllHotels();

            assertThat(result).hasSize(1).containsExactly(sampleShortDto);
            verify(hotelRepository).findAll();
            verify(hotelMapper).toShortDtoList(List.of(sampleHotel));
        }
    }

    @Nested
    @DisplayName("getHotelById()")
    class GetHotelByIdTests {

        @Test
        @DisplayName("Should return full hotel DTO when hotel exists")
        void shouldReturnHotelFullDtoWhenFound() {
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(sampleHotel));
            when(hotelMapper.toFullDto(sampleHotel)).thenReturn(sampleFullDto);

            HotelFullDto result = hotelService.getHotelById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(hotelRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw HotelNotFoundException when hotel does not exist")
        void shouldThrowExceptionWhenNotFound() {
            when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> hotelService.getHotelById(99L))
                    .isInstanceOf(HotelNotFoundException.class)
                    .hasMessageContaining("99");

            verify(hotelRepository).findById(99L);
            verify(hotelMapper, never()).toFullDto(any());
        }
    }

    @Nested
    @DisplayName("createHotel()")
    class CreateHotelTests {

        @Test
        @DisplayName("Should map DTO, save entity, and return short DTO")
        void shouldCreateHotelSuccessfully() {
            CreateHotelDto createDto = CreateHotelDto.builder()
                    .name("DoubleTree by Hilton")
                    .brand("Hilton")
                    .build();

            when(hotelMapper.toEntity(createDto)).thenReturn(sampleHotel);
            when(hotelRepository.save(sampleHotel)).thenReturn(sampleHotel);
            when(hotelMapper.toShortDto(sampleHotel)).thenReturn(sampleShortDto);

            HotelShortDto result = hotelService.createHotel(createDto);

            assertThat(result).isEqualTo(sampleShortDto);
            verify(hotelMapper).toEntity(createDto);
            verify(hotelRepository).save(sampleHotel);
            verify(hotelMapper).toShortDto(sampleHotel);
        }
    }

    @Nested
    @DisplayName("addAmenitiesToHotel()")
    class AddAmenitiesTests {

        @Test
        @DisplayName("Should attach existing and new amenities to hotel")
        void shouldAddAmenitiesToHotel() {
            Amenity existingAmenity = Amenity.builder().id(10L).name("WiFi").build();
            Amenity newAmenity = Amenity.builder().id(11L).name("Gym").build();

            when(hotelRepository.findById(1L)).thenReturn(Optional.of(sampleHotel));
            when(amenityRepository.findByName("WiFi")).thenReturn(Optional.of(existingAmenity));
            when(amenityRepository.findByName("Gym")).thenReturn(Optional.empty());
            when(amenityRepository.save(any(Amenity.class))).thenReturn(newAmenity);

            hotelService.addAmenitiesToHotel(1L, List.of("WiFi", "Gym"));

            assertThat(sampleHotel.getAmenities()).hasSize(2);
            verify(hotelRepository).save(sampleHotel);
        }

        @Test
        @DisplayName("Should throw HotelNotFoundException when hotel ID is invalid")
        void shouldThrowExceptionWhenHotelNotFoundForAmenities() {
            when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> hotelService.addAmenitiesToHotel(99L, List.of("WiFi")))
                    .isInstanceOf(HotelNotFoundException.class);

            verify(amenityRepository, never()).findByName(any());
        }

        @Test
        @DisplayName("Should do nothing when amenities list is null or empty")
        void shouldDoNothingWhenAmenitiesListIsEmpty() {
            hotelService.addAmenitiesToHotel(1L, null);
            hotelService.addAmenitiesToHotel(1L, List.of());

            verify(hotelRepository, never()).findById(any());
        }
    }
}