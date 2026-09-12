package by.mashnyuk.hotels.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed address of the hotel")
public class AddressDto {

    @NotNull(message = "House number is required")
    @Positive(message = "House number must be positive")
    @Schema(description = "Building or house number", example = "9", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer houseNumber;

    @NotBlank(message = "Street is required")
    @Schema(description = "Street name", example = "Pobediteley Avenue", requiredMode = Schema.RequiredMode.REQUIRED)
    private String street;

    @NotBlank(message = "City is required")
    @Schema(description = "City name", example = "Minsk", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank(message = "Country is required")
    @Schema(description = "Country name", example = "Belarus", requiredMode = Schema.RequiredMode.REQUIRED)
    private String country;

    @NotBlank(message = "Post code is required")
    @Schema(description = "Postal/ZIP code", example = "220004", requiredMode = Schema.RequiredMode.REQUIRED)
    private String postCode;
}
