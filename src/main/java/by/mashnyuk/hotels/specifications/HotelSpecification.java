package by.mashnyuk.hotels.specifications;


import by.mashnyuk.hotels.model.Hotel;
import by.mashnyuk.hotels.model.dto.request.HotelSearchCriteria;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@UtilityClass
public class HotelSpecification {

    public static Specification<Hotel> build(HotelSearchCriteria searchCriteria) {
        return Specification.where(containsNameCaseInsensitive(searchCriteria.getName()))
                .and(hasBrandCaseInsensitive(searchCriteria.getBrand()))
                .and(hasCityCaseInsensitive(searchCriteria.getCity()))
                .and(hasCountryCaseInsensitive(searchCriteria.getCountry()))
                .and(hasAmenities(searchCriteria.getAmenities()));
    }

    private static Specification<Hotel> containsNameCaseInsensitive(String name) {
        return SpecificationUtils.likeIgnoreCase("name", name);
    }

    private static Specification<Hotel> hasBrandCaseInsensitive(String brand) {
        return SpecificationUtils.equalIgnoreCase("brand", brand);
    }

    private static Specification<Hotel> hasCityCaseInsensitive(String city) {
        return SpecificationUtils.equalIgnoreCaseNested("address", "city", city);
    }

    private static Specification<Hotel> hasCountryCaseInsensitive(String country) {
        return SpecificationUtils.equalIgnoreCaseNested("address", "country", country);
    }

    private static Specification<Hotel> hasAmenities(List<String> amenities) {
        return SpecificationUtils.containsAllInCollection("amenities", amenities);
    }
}
