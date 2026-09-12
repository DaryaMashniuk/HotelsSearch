package by.mashnyuk.hotels.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hotel contact details")
public class ContactsDto {

    @NotBlank(message = "Phone number is required")
    @Schema(description = "Contact phone number", example = "+375 17 309-80-00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Contact email address", example = "doubletreeminsk.info@hilton.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}