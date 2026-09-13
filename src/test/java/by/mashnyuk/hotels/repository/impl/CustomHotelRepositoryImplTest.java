package by.mashnyuk.hotels.repository.impl;

import by.mashnyuk.hotels.exceptions.IllegalArgumentCustomException;
import by.mashnyuk.hotels.model.Address;
import by.mashnyuk.hotels.model.Amenity;
import by.mashnyuk.hotels.model.ArrivalTime;
import by.mashnyuk.hotels.model.Contacts;
import by.mashnyuk.hotels.model.Hotel;
import by.mashnyuk.hotels.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("h2")
class CustomHotelRepositoryImplTest {

    @Autowired
    private HotelRepository hotelRepository;

    @BeforeEach
    void setUp() {
        Amenity wifi = new Amenity("Free WiFi");
        Amenity fitness= new Amenity("Fitness center");
        Amenity parking = new Amenity("Free parking");
        Amenity rooms = new Amenity("Non-smoking rooms");

        Hotel hotel1 = Hotel.builder()
                .name("DoubleTree by Hilton")
                .brand("Hilton")
                .address(Address.builder()
                        .houseNumber(9)
                        .street("Pobediteley Ave")
                        .postCode("220004")
                        .city("Minsk")
                        .country("Belarus")
                        .build())
                .contacts(Contacts.builder()
                        .phone("+1234567890")
                        .email("contact@grandhotel.com")
                        .build())
                .amenities(Set.of(
                        wifi,
                        fitness,
                        parking
                ))
                .arrivalTime(ArrivalTime.builder()
                        .checkIn("12:00")
                        .build())
                .build();

        Hotel hotel2 = Hotel.builder()
                .name("Hampton by Hilton")
                .brand("Hilton")
                .address(Address.builder()
                        .houseNumber(8)
                        .street("Tolstogo St")
                        .postCode("220007")
                        .city("Minsk")
                        .country("Belarus")
                        .build())
                .contacts(Contacts.builder()
                        .phone("+1987654321")
                        .email("info@plazaresort.com")
                        .build())
                .amenities(Set.of(
                        wifi,
                        rooms
                ))
                .arrivalTime(ArrivalTime.builder()
                        .checkIn("12:00")
                        .build())
                .build();

        Hotel hotel3 = Hotel.builder()
                .name("Marriott Hotel")
                .brand("Marriott")
                .address(Address.builder()
                        .houseNumber(20)
                        .street("Tverskaya St")
                        .postCode("101000")
                        .city("Moscow")
                        .country("Russia")
                        .build())
                .contacts(Contacts.builder()
                        .phone("+1987654321")
                        .email("info@plazaresort.com")
                        .build())
                .amenities(Set.of(
                        wifi,
                        fitness
                ))
                .arrivalTime(ArrivalTime.builder()
                        .checkIn("12:00")
                        .build())
                .build();

        hotelRepository.saveAllAndFlush(Set.of(hotel1, hotel2, hotel3));
    }

    @Nested
    @DisplayName("Repository Histogram Generation Tests")
    class GetHistogramByAttributeTests {

        @Test
        @DisplayName("Should correctly aggregate count grouped by brand")
        void shouldGroupByBrand() {
            Map<String, Long> histogram = hotelRepository.getHistogramByAttribute("brand");

            assertThat(histogram)
                    .hasSize(2)
                    .containsEntry("Hilton", 2L)
                    .containsEntry("Marriott", 1L);
        }

        @Test
        @DisplayName("Should correctly aggregate count grouped by city")
        void shouldGroupByCity() {
            Map<String, Long> histogram = hotelRepository.getHistogramByAttribute("city");

            assertThat(histogram)
                    .hasSize(2)
                    .containsEntry("Minsk", 2L)
                    .containsEntry("Moscow", 1L);
        }

        @Test
        @DisplayName("Should correctly aggregate count grouped by country")
        void shouldGroupByCountry() {
            Map<String, Long> histogram = hotelRepository.getHistogramByAttribute("country");

            assertThat(histogram)
                    .hasSize(2)
                    .containsEntry("Belarus", 2L)
                    .containsEntry("Russia", 1L);
        }

        @Test
        @DisplayName("Should correctly perform JOIN and group by amenities collection")
        void shouldGroupByAmenities() {
            Map<String, Long> histogram = hotelRepository.getHistogramByAttribute("amenities");

            assertThat(histogram)
                    .hasSize(4)
                    .containsEntry("Free WiFi", 3L)
                    .containsEntry("Fitness center", 2L)
                    .containsEntry("Free parking", 1L)
                    .containsEntry("Non-smoking rooms", 1L);
        }

        @Test
        @DisplayName("Should return empty map when database contains no hotels")
        void shouldReturnEmptyMapWhenNoData() {
            hotelRepository.deleteAll();

            Map<String, Long> histogram = hotelRepository.getHistogramByAttribute("brand");

            assertThat(histogram).isEmpty();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentCustomException when invalid attribute name is passed")
        void shouldThrowExceptionForInvalidParam() {
            assertThatThrownBy(() -> hotelRepository.getHistogramByAttribute("unsupported_param"))
                    .isInstanceOf(IllegalArgumentCustomException.class)
                    .hasMessageContaining("Unsupported histogram parameter: unsupported_param");
        }
    }
}