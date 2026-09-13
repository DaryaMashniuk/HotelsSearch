package by.mashnyuk.hotels.repository.impl;

import by.mashnyuk.hotels.exceptions.IllegalArgumentCustomException;
import by.mashnyuk.hotels.model.Amenity;
import by.mashnyuk.hotels.model.Hotel;
import by.mashnyuk.hotels.repository.CustomHotelRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CustomHotelRepositoryImpl implements CustomHotelRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Long> getHistogramByAttribute(String param) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Hotel> hotel = query.from(Hotel.class);

        Path<String> groupPath;

        switch (param) {
            case "brand" -> groupPath = hotel.get("brand");
            case "city" -> groupPath = hotel.get("address").get("city");
            case "country" -> groupPath = hotel.get("address").get("country");
            case "amenities" -> {
                Join<Hotel, Amenity> amenityJoin = hotel.join("amenities");
                groupPath = amenityJoin.get("name");
            }
            default -> throw new IllegalArgumentCustomException("Unsupported histogram parameter: " + param);
        }

        query.select(cb.tuple(groupPath.alias("key"), cb.count(hotel).alias("value")));
        query.groupBy(groupPath);

        List<Tuple> results = entityManager.createQuery(query).getResultList();

        return results.stream().collect(Collectors.toMap(
                t -> t.get("key", String.class),
                t -> t.get("value", Long.class)
        ));
    }
}