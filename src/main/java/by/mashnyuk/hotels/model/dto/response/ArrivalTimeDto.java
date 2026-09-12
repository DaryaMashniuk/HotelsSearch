package by.mashnyuk.hotels.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Check-in and check-out schedule")
public class ArrivalTimeDto {

    @NotBlank(message = "Check-in time is required")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Check-in time must be in HH:mm format")
    @Schema(description = "Standard check-in time (HH:mm format)", example = "14:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String checkIn;

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Check-out time must be in HH:mm format")
    @Schema(description = "Standard check-out time (HH:mm format) - optional field", example = "12:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String checkOut;
}