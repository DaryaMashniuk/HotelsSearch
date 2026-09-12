package by.mashnyuk.hotels.mapper;

import by.mashnyuk.hotels.model.Address;
import by.mashnyuk.hotels.model.Amenity;
import by.mashnyuk.hotels.model.ArrivalTime;
import by.mashnyuk.hotels.model.Contacts;
import by.mashnyuk.hotels.model.Hotel;
import by.mashnyuk.hotels.model.dto.response.AddressDto;
import by.mashnyuk.hotels.model.dto.response.ArrivalTimeDto;
import by.mashnyuk.hotels.model.dto.response.ContactsDto;
import by.mashnyuk.hotels.model.dto.request.CreateHotelDto;
import by.mashnyuk.hotels.model.dto.response.HotelFullDto;
import by.mashnyuk.hotels.model.dto.response.HotelShortDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class HotelMapperTest {

    private final HotelMapper hotelMapper = Mappers.getMapper(HotelMapper.class);

    @Nested
    @DisplayName("Hotel -> HotelShortDto Mappings")
    class HotelToShortDtoTests {

        @Test
        @DisplayName("Should map Hotel entity to HotelShortDto with correctly formatted address")
        void shouldMapHotelToHotelShortDto() {
            Hotel hotel = createSampleHotelEntity();

            HotelShortDto result = hotelMapper.toShortDto(hotel);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("DoubleTree by Hilton Minsk");
            assertThat(result.getDescription()).isEqualTo("Luxurious hotel in city center");
            assertThat(result.getPhone()).isEqualTo("+375 17 309-80-00");
            assertThat(result.getAddress()).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
        }

        @Test
        @DisplayName("Should map list of Hotel entities to list of HotelShortDto")
        void shouldMapHotelListToHotelShortDtoList() {
            List<Hotel> hotels = List.of(createSampleHotelEntity());

            List<HotelShortDto> results = hotelMapper.toShortDtoList(hotels);

            assertThat(results)
                    .isNotNull()
                    .hasSize(1);
            assertThat(results.getFirst().getName()).isEqualTo("DoubleTree by Hilton Minsk");
        }

        @Test
        @DisplayName("Should handle null address gracefully when mapping to HotelShortDto")
        void shouldHandleNullAddressInShortDto() {
            Hotel hotel = createSampleHotelEntity();
            hotel.setAddress(null);

            HotelShortDto result = hotelMapper.toShortDto(hotel);

            assertThat(result).isNotNull();
            assertThat(result.getAddress()).isNull();
        }

        @Test
        @DisplayName("Should return null when mapping null Hotel entity to HotelShortDto")
        void shouldReturnNullWhenHotelIsNull() {
            assertThat(hotelMapper.toShortDto(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Hotel -> HotelFullDto Mappings")
    class HotelToFullDtoTests {

        @Test
        @DisplayName("Should map Hotel entity to HotelFullDto with all nested objects and amenities")
        void shouldMapHotelToHotelFullDto() {
            Hotel hotel = createSampleHotelEntity();

            HotelFullDto result = hotelMapper.toFullDto(hotel);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("DoubleTree by Hilton Minsk");
            assertThat(result.getBrand()).isEqualTo("Hilton");

            assertThat(result.getAddress()).isNotNull();
            assertThat(result.getAddress().getCity()).isEqualTo("Minsk");
            assertThat(result.getAddress().getHouseNumber()).isEqualTo(9);

            assertThat(result.getContacts()).isNotNull();
            assertThat(result.getContacts().getEmail()).isEqualTo("doubletree@hilton.com");

            assertThat(result.getArrivalTime()).isNotNull();
            assertThat(result.getArrivalTime().getCheckIn()).isEqualTo("14:00");
            assertThat(result.getArrivalTime().getCheckOut()).isEqualTo("12:00");

            assertThat(result.getAmenities())
                    .containsExactlyInAnyOrder("Free WiFi", "Fitness center");
        }

        @Test
        @DisplayName("Should return empty list for amenities when entity amenities list is null")
        void shouldHandleNullAmenitiesList() {
            Hotel hotel = createSampleHotelEntity();
            hotel.setAmenities(null);

            HotelFullDto result = hotelMapper.toFullDto(hotel);

            assertThat(result).isNotNull();
            assertThat(result.getAmenities()).isEmpty();
        }
    }

    @Nested
    @DisplayName("CreateHotelDto -> Hotel Mappings")
    class CreateHotelDtoToEntityTests {

        @Test
        @DisplayName("Should map CreateHotelDto to Hotel entity (ignoring id and amenities)")
        void shouldMapCreateHotelDtoToHotelEntity() {
            CreateHotelDto dto = CreateHotelDto.builder()
                    .name("DoubleTree by Hilton Minsk")
                    .description("Luxurious hotel in city center")
                    .brand("Hilton")
                    .address(AddressDto.builder()
                            .houseNumber(9)
                            .street("Pobediteley Avenue")
                            .city("Minsk")
                            .country("Belarus")
                            .postCode("220004")
                            .build())
                    .contacts(ContactsDto.builder()
                            .phone("+375 17 309-80-00")
                            .email("doubletree@hilton.com")
                            .build())
                    .arrivalTime(ArrivalTimeDto.builder()
                            .checkIn("14:00")
                            .checkOut("12:00")
                            .build())
                    .build();

            Hotel entity = hotelMapper.toEntity(dto);

            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isNull();
            assertThat(entity.getAmenities()).isNullOrEmpty();
            assertThat(entity.getName()).isEqualTo("DoubleTree by Hilton Minsk");
            assertThat(entity.getBrand()).isEqualTo("Hilton");

            assertThat(entity.getAddress()).isNotNull();
            assertThat(entity.getAddress().getStreet()).isEqualTo("Pobediteley Avenue");

            assertThat(entity.getContacts()).isNotNull();
            assertThat(entity.getContacts().getPhone()).isEqualTo("+375 17 309-80-00");

            assertThat(entity.getArrivalTime()).isNotNull();
            assertThat(entity.getArrivalTime().getCheckIn()).isEqualTo("14:00");
        }
    }

    private Hotel createSampleHotelEntity() {
        return Hotel.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .description("Luxurious hotel in city center")
                .brand("Hilton")
                .address(Address.builder()
                        .houseNumber(9)
                        .street("Pobediteley Avenue")
                        .city("Minsk")
                        .postCode("220004")
                        .country("Belarus")
                        .build())
                .contacts(Contacts.builder()
                        .phone("+375 17 309-80-00")
                        .email("doubletree@hilton.com")
                        .build())
                .arrivalTime(ArrivalTime.builder()
                        .checkIn("14:00")
                        .checkOut("12:00")
                        .build())
                .amenities(Set.of(
                        Amenity.builder().id(10L).name("Free WiFi").build(),
                        Amenity.builder().id(11L).name("Fitness center").build()
                ))
                .build();
    }
}