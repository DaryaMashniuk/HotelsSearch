package by.mashnyuk.hotels.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Summary representation of a hotel")
public class HotelShortDto {

    @Schema(description = "Unique identifier of the hotel", example = "1")
    private Long id;

    @Schema(description = "Name of the hotel", example = "DoubleTree by Hilton Minsk")
    private String name;

    @Schema(description = "Detailed description of the hotel", example = "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms...")
    private String description;

    @Schema(description = "Formatted single-string address", example = "9 Pobediteley Avenue, Minsk, 220004, Belarus")
    private String address;

    @Schema(description = "Primary contact phone number", example = "+375 17 309-80-00")
    private String phone;
}
