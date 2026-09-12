package by.mashnyuk.hotels.specifications;

import jakarta.persistence.criteria.Join;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

@UtilityClass
public class SpecificationUtils {

    public static <T> Specification<T> likeIgnoreCase(String field, String value) {
        return (root, query, cb) ->
                value == null || value.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    public static <T> Specification<T> equalIgnoreCase(String field, String value) {
        return (root, query, cb) ->
                value == null || value.isBlank()
                        ? null
                        : cb.equal(cb.lower(root.get(field)), value.toLowerCase());
    }

    public static <T> Specification<T> equalIgnoreCaseNested(String parentField, String childField, String value) {
        return (root, query, cb) ->
                value == null || value.isBlank()
                        ? null
                        : cb.equal(cb.lower(root.get(parentField).get(childField)), value.toLowerCase());
    }

    public static <T, E> Specification<T> containsAllInCollection(String collectionField, List<String> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return null;
            }
            query.distinct(true);
            var predicates = values.stream()
                    .filter(val -> val != null && !val.isBlank())
                    .map(val -> {
                        Join<T, E> join = root.join(collectionField);
                        return cb.equal(cb.lower(join.as(String.class)), val.toLowerCase());
                    })
                    .toArray(jakarta.persistence.criteria.Predicate[]::new);

            return predicates.length == 0 ? null : cb.and(predicates);
        };
    }
}
