package by.mashnyuk.hotels.repository;

import by.mashnyuk.hotels.model.Address;
import by.mashnyuk.hotels.model.Amenity;
import by.mashnyuk.hotels.model.ArrivalTime;
import by.mashnyuk.hotels.model.Contacts;
import by.mashnyuk.hotels.model.Hotel;
import by.mashnyuk.hotels.model.dto.request.HotelSearchCriteria;
import by.mashnyuk.hotels.specifications.HotelSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
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
    @DisplayName("Tests for HotelSpecification Dynamic Search")
    class HotelSpecificationTests {

        private Hotel hotel1;
        private Hotel hotel2;
        private Hotel hotel3;

        void seedSearchData() {
            Amenity wifi = entityManager.persist(Amenity.builder().name("Free Wi-Fi").build());
            Amenity pool = entityManager.persist(Amenity.builder().name("Swimming Pool").build());
            Amenity parking = entityManager.persist(Amenity.builder().name("Parking").build());

            hotel1 = createSampleHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk");
            hotel1.addAmenity(wifi);
            hotel1.addAmenity(pool);
            hotel1.addAmenity(parking);

            hotel2 = createSampleHotel("Hilton Garden Inn Grodno", "Hilton", "Grodno");
            hotel2.addAmenity(wifi);

            hotel3 = createSampleHotel("Renaissance Minsk Hotel", "Marriott", "Minsk");
            hotel3.addAmenity(wifi);
            hotel3.addAmenity(pool);

            hotelRepository.saveAll(List.of(hotel1, hotel2, hotel3));
            flushAndClear();
        }

        @Test
        @DisplayName("Should return all hotels when criteria is null or empty")
        void givenEmptyCriteria_whenFindAll_thenReturnAllHotels() {
            seedSearchData();

            Specification<Hotel> spec = HotelSpecification.build(null);
            List<Hotel> result = hotelRepository.findAll(spec);

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("Should filter by partial name case-insensitive")
        void givenNameCriteria_whenFindAll_thenReturnMatchingHotels() {
            seedSearchData();

            HotelSearchCriteria criteria = HotelSearchCriteria.builder()
                    .name("hilton")
                    .build();

            Specification<Hotel> spec = HotelSpecification.build(criteria);
            List<Hotel> result = hotelRepository.findAll(spec);

            assertThat(result).hasSize(2)
                    .extracting(Hotel::getName)
                    .containsExactlyInAnyOrder("DoubleTree by Hilton Minsk", "Hilton Garden Inn Grodno");
        }

        @Test
        @DisplayName("Should filter by brand case-insensitive")
        void givenBrandCriteria_whenFindAll_thenReturnMatchingHotels() {
            seedSearchData();

            HotelSearchCriteria criteria = HotelSearchCriteria.builder()
                    .brand("marriott")
                    .build();

            Specification<Hotel> spec = HotelSpecification.build(criteria);
            List<Hotel> result = hotelRepository.findAll(spec);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Renaissance Minsk Hotel");
        }

        @Test
        @DisplayName("Should filter by embedded address city and country")
        void givenCityAndCountryCriteria_whenFindAll_thenReturnMatchingHotels() {
            seedSearchData();

            HotelSearchCriteria criteria = HotelSearchCriteria.builder()
                    .city("minsk")
                    .country("belarus")
                    .build();

            Specification<Hotel> spec = HotelSpecification.build(criteria);
            List<Hotel> result = hotelRepository.findAll(spec);

            assertThat(result).hasSize(2)
                    .extracting(Hotel::getName)
                    .containsExactlyInAnyOrder("DoubleTree by Hilton Minsk", "Renaissance Minsk Hotel");
        }

        @Test
        @DisplayName("Should filter by single amenity")
        void givenSingleAmenityCriteria_whenFindAll_thenReturnHotelsWithAmenity() {
            seedSearchData();

            HotelSearchCriteria criteria = HotelSearchCriteria.builder()
                    .amenities(List.of("parking"))
                    .build();

            Specification<Hotel> spec = HotelSpecification.build(criteria);
            List<Hotel> result = hotelRepository.findAll(spec);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("DoubleTree by Hilton Minsk");
        }

        @Test
        @DisplayName("Should filter by multiple amenities using AND logic")
        void givenMultipleAmenitiesCriteria_whenFindAll_thenReturnHotelsWithAllAmenities() {
            seedSearchData();

            HotelSearchCriteria criteria = HotelSearchCriteria.builder()
                    .amenities(List.of("free wi-fi", "swimming pool"))
                    .build();

            Specification<Hotel> spec = HotelSpecification.build(criteria);
            List<Hotel> result = hotelRepository.findAll(spec);

            assertThat(result).hasSize(2)
                    .extracting(Hotel::getName)
                    .containsExactlyInAnyOrder("DoubleTree by Hilton Minsk", "Renaissance Minsk Hotel");
        }

        @Test
        @DisplayName("Should filter by complex combination of all fields")
        void givenAllCriteriaFields_whenFindAll_thenReturnExactMatch() {
            seedSearchData();

            HotelSearchCriteria criteria = HotelSearchCriteria.builder()
                    .name("doubletree")
                    .brand("hilton")
                    .city("minsk")
                    .country("belarus")
                    .amenities(List.of("parking", "swimming pool"))
                    .build();

            Specification<Hotel> spec = HotelSpecification.build(criteria);
            List<Hotel> result = hotelRepository.findAll(spec);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("DoubleTree by Hilton Minsk");
        }

        @Test
        @DisplayName("Should return empty list when combined criteria matches no entity")
        void givenNonMatchingCombinedCriteria_whenFindAll_thenReturnEmptyList() {
            seedSearchData();

            HotelSearchCriteria criteria = HotelSearchCriteria.builder()
                    .brand("hilton")
                    .city("minsk")
                    .amenities(List.of("non-existent-amenity"))
                    .build();

            Specification<Hotel> spec = HotelSpecification.build(criteria);
            List<Hotel> result = hotelRepository.findAll(spec);

            assertThat(result).isEmpty();
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