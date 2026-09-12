package by.mashnyuk.hotels.repository;


import by.mashnyuk.hotels.model.Amenity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("h2")
@DisplayName("Integration Tests for AmenityRepository")
class AmenityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AmenityRepository amenityRepository;

    @Test
    @DisplayName("Should find Amenity by name ignoring case if configured or exact match")
    void shouldFindAmenityByName() {
        Amenity amenity = Amenity.builder()
                .name("Free WiFi")
                .build();
        entityManager.persistAndFlush(amenity);

        Optional<Amenity> found = amenityRepository.findByName("Free WiFi");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Free WiFi");
    }

    @Test
    @DisplayName("Should return empty Optional when Amenity name does not exist")
    void shouldReturnEmptyWhenNameNotFound() {
        Optional<Amenity> found = amenityRepository.findByName("NonExistent");

        assertThat(found).isEmpty();
    }
}
