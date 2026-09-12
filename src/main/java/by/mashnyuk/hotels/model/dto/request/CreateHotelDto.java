package by.mashnyuk.hotels.model.dto.request;

import by.mashnyuk.hotels.model.dto.response.AddressDto;
import by.mashnyuk.hotels.model.dto.response.ArrivalTimeDto;
import by.mashnyuk.hotels.model.dto.response.ContactsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating a new hotel record")
public class CreateHotelDto {

    @NotBlank(message = "Hotel name is required")
    @Schema(description = "Name of the hotel", example = "DoubleTree by Hilton Minsk", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Detailed description of the hotel (optional)", example = "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms...", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @NotBlank(message = "Brand is required")
    @Schema(description = "Hotel brand or chain", example = "Hilton", requiredMode = Schema.RequiredMode.REQUIRED)
    private String brand;

    @NotNull(message = "Address details are required")
    @Valid
    @Schema(description = "Detailed address payload", requiredMode = Schema.RequiredMode.REQUIRED)
    private AddressDto address;

    @NotNull(message = "Contacts details are required")
    @Valid
    @Schema(description = "Contact information payload", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContactsDto contacts;

    @NotNull(message = "Arrival time details are required")
    @Valid
    @Schema(description = "Check-in/Check-out schedule payload", requiredMode = Schema.RequiredMode.REQUIRED)
    private ArrivalTimeDto arrivalTime;
}
