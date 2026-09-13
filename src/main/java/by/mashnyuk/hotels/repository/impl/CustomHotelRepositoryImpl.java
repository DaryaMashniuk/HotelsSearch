package by.mashnyuk.hotels.repository.impl;

import by.mashnyuk.hotels.exceptions.IllegalArgumentCustomException;
import by.mashnyuk.hotels.model.Hotel;
import by.mashnyuk.hotels.repository.CustomHotelRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

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
        Root<Hotel> root = query.from(Hotel.class);

        Expression<String> groupByExpression = resolvePath(root, param);

        query.select(cb.tuple(
                groupByExpression.alias("key"),
                cb.count(root).alias("count")
        ));

        query.where(cb.isNotNull(groupByExpression));
        query.groupBy(groupByExpression);

        return entityManager.createQuery(query)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get("key", String.class),
                        tuple -> tuple.get("count", Long.class)
                ));
    }

    private Expression<String> resolvePath(Root<Hotel> root, String param) {
        return switch (param.toLowerCase()) {
            case "brand" -> root.get("brand");
            case "city" -> root.get("address").get("city");
            case "country" -> root.get("address").get("country");
            case "amenities" -> root.join("amenities");
            default -> throw new IllegalArgumentCustomException("Unsupported histogram parameter: " + param);
        };
    }
}