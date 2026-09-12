package by.mashnyuk.hotels.repository;

import by.mashnyuk.hotels.model.Address;
import by.mashnyuk.hotels.model.Amenity;
import by.mashnyuk.hotels.model.ArrivalTime;
import by.mashnyuk.hotels.model.Contacts;
import by.mashnyuk.hotels.model.Hotel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("h2")
@DisplayName("Integration Tests for HotelRepository")
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("Tests for findByBrand")
    class FindByBrandTests {

        @Test
        @DisplayName("Should find hotels matching specific brand")
        void givenSavedHotel_whenFindByBrand_thenReturnMatchingHotelList() {
            Hotel hotel = createSampleHotel("Minsk Marriott Hotel", "Marriott", "Minsk");
            hotelRepository.save(hotel);

            flushAndClear();

            List<Hotel> foundHotels = hotelRepository.findByBrand("Marriott");

            assertThat(foundHotels).hasSize(1);
            assertThat(foundHotels.get(0).getName()).isEqualTo("Minsk Marriott Hotel");
            assertThat(foundHotels.get(0).getBrand()).isEqualTo("Marriott");
        }

        @Test
        @DisplayName("Should return empty list when brand does not exist")
        void givenSavedHotel_whenFindByNonExistentBrand_thenReturnEmptyList() {
            Hotel hotel = createSampleHotel("Minsk Marriott Hotel", "Marriott", "Minsk");
            hotelRepository.save(hotel);

            flushAndClear();

            List<Hotel> foundHotels = hotelRepository.findByBrand("Hilton");

            assertThat(foundHotels).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tests for findByAddressCity")
    class FindByAddressCityTests {

        @Test
        @DisplayName("Should find hotels by city mapped inside embedded Address")
        void givenSavedHotel_whenFindByAddressCity_thenReturnHotelsInThatCity() {
            Hotel hotel1 = createSampleHotel("DoubleTree by Hilton", "Hilton", "Minsk");
            Hotel hotel2 = createSampleHotel("Hotel Europe", "Independent", "Minsk");
            Hotel hotel3 = createSampleHotel("Beijing Hotel", "Beijing", "Gomel");

            hotelRepository.saveAll(List.of(hotel1, hotel2, hotel3));

            flushAndClear();

            List<Hotel> minskHotels = hotelRepository.findByAddressCity("Minsk");

            assertThat(minskHotels).hasSize(2)
                    .extracting(h -> h.getAddress().getCity())
                    .containsOnly("Minsk");
        }

        @Test
        @DisplayName("Should return empty list when no hotels exist in target city")
        void givenSavedHotels_whenFindByCityWithNoHotels_thenReturnEmptyList() {
            Hotel hotel = createSampleHotel("Minsk Marriott Hotel", "Marriott", "Minsk");
            hotelRepository.save(hotel);

            flushAndClear();

            List<Hotel> result = hotelRepository.findByAddressCity("Brest");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tests for findWithAmenitiesById")
    class FindWithAmenitiesByIdTests {

        @Test
        @DisplayName("Should fetch hotel along with amenities in a single query using EntityGraph")
        void givenHotelWithAmenities_whenFindWithAmenitiesById_thenEagerlyFetchAmenities() {
            Amenity wifi = Amenity.builder().name("Free Wi-Fi").build();
            Amenity pool = Amenity.builder().name("Swimming Pool").build();
            entityManager.persist(wifi);
            entityManager.persist(pool);

            Hotel hotel = createSampleHotel("Minsk Marriott Hotel", "Marriott", "Minsk");
            hotel.addAmenity(wifi);
            hotel.addAmenity(pool);

            Hotel savedHotel = hotelRepository.save(hotel);

            flushAndClear();

            Optional<Hotel> foundHotel = hotelRepository.findWithAmenitiesById(savedHotel.getId());

            assertThat(foundHotel).isPresent();
            assertThat(foundHotel.get().getAmenities())
                    .hasSize(2)
                    .extracting(Amenity::getName)
                    .containsExactlyInAnyOrder("Free Wi-Fi", "Swimming Pool");
        }

        @Test
        @DisplayName("Should fetch hotel with empty amenities set if no amenities attached")
        void givenHotelWithoutAmenities_whenFindWithAmenitiesById_thenReturnHotelWithEmptySet() {
            Hotel hotel = createSampleHotel("DoubleTree by Hilton", "Hilton", "Minsk");
            Hotel saved = hotelRepository.save(hotel);

            flushAndClear();

            Optional<Hotel> result = hotelRepository.findWithAmenitiesById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getAmenities()).isEmpty();
        }

        @Test
        @DisplayName("Should save and cascade new Amenity via PERSIST on Hotel save")
        void givenHotelWithNewAmenity_whenSave_thenCascadePersistAmenity() {
            Amenity newAmenity = Amenity.builder().name("Fitness Center").build();
            Hotel hotel = createSampleHotel("Fitness Hotel", "Brand", "Minsk");
            hotel.addAmenity(newAmenity);

            Hotel saved = hotelRepository.save(hotel);

            flushAndClear();

            Optional<Hotel> fetched = hotelRepository.findWithAmenitiesById(saved.getId());

            assertThat(fetched).isPresent();
            assertThat(fetched.get().getAmenities())
                    .extracting(Amenity::getName)
                    .contains("Fitness Center");
        }
    }

    @Nested
    @DisplayName("Tests for Database Constraints")
    class DatabaseConstraintTests {

        @Test
        @DisplayName("Should throw DataIntegrityViolationException when saving duplicate hotel name")
        void givenExistingHotelName_whenSaveDuplicate_thenThrowException() {
            Hotel hotel1 = createSampleHotel("Same Name Hotel", "Brand A", "Minsk");
            Hotel hotel2 = createSampleHotel("Same Name Hotel", "Brand B", "Grodno");

            hotelRepository.save(hotel1);

            assertThatThrownBy(() -> {
                hotelRepository.save(hotel2);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private Hotel createSampleHotel(String name, String brand, String city) {
        Address address = Address.builder()
                .houseNumber(12)
                .street("Pobediteley Ave")
                .city(city)
                .country("Belarus")
                .postCode("220004")
                .build();

        Contacts contacts = Contacts.builder()
                .phone("+375173098000")
                .email("info.minsk@marriott.com")
                .build();

        ArrivalTime arrivalTime = ArrivalTime.builder()
                .checkIn("15:00")
                .checkOut("12:00")
                .build();

        return Hotel.builder()
                .name(name)
                .description("Luxury hotel next to the river")
                .brand(brand)
                .address(address)
                .contacts(contacts)
                .arrivalTime(arrivalTime)
                .build();
    }
}