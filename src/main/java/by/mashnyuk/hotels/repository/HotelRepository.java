package by.mashnyuk.hotels.repository;

import by.mashnyuk.hotels.model.Hotel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel>, CustomHotelRepository{
    @EntityGraph(attributePaths = {"amenities"})
    Optional<Hotel> findWithAmenitiesById(Long id);

    List<Hotel> findByBrand(String marriott);

    List<Hotel> findByAddressCity(String minsk);

    boolean existsByName(String name);
}
