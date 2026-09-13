package by.mashnyuk.hotels.model.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HotelSearchCriteria {
    private String name;
    private String brand;
    private String city;
    private String country;
    private List<String> amenities;
}
