package by.mashnyuk.hotels.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed information response for a specific hotel")
public class HotelFullDto {

    @Schema(description = "Unique identifier of the hotel", example = "1")
    private Long id;

    @Schema(description = "Name of the hotel", example = "DoubleTree by Hilton Minsk")
    private String name;

    @Schema(description = "Detailed description of the hotel", example = "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms...")
    private String description;

    @Schema(description = "Hotel brand or chain", example = "Hilton")
    private String brand;

    @Schema(description = "Detailed address object")
    private AddressDto address;

    @Schema(description = "Contact information")
    private ContactsDto contacts;

    @Schema(description = "Arrival and departure times")
    private ArrivalTimeDto arrivalTime;

    @Schema(description = "List of hotel amenities", example = "[\"Free parking\", \"Free WiFi\", \"Fitness center\"]")
    private List<String> amenities;
}
