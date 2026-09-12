package by.mashnyuk.hotels.model.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class HotelSearchCriteria {
    private String name;
    private String brand;
    private String city;
    private String country;
    private List<String> amenities;
}
