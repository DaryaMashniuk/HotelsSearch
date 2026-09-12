package by.mashnyuk.hotels.repository;

import by.mashnyuk.hotels.model.Hotel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    @EntityGraph(attributePaths = {"amenities"})
    Optional<Hotel> findWithAmenitiesById(Long id);

    List<Hotel> findByBrand(String marriott);

    List<Hotel> findByAddressCity(String minsk);
}
