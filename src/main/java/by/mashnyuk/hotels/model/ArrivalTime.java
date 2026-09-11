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
public class ArrivalTime {

    @Column(name = "check_in", nullable = false)
    private String checkIn;

    @Column(name = "check_out")
    private String checkOut;

}
