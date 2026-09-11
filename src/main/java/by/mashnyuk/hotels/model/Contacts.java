package by.mashnyuk.hotels.model;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contacts {
    @Column(name = "phone", nullable = false, length = 100)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;
}
