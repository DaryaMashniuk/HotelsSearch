package by.mashnyuk.hotels.integration;

import by.mashnyuk.hotels.model.dto.request.CreateHotelDto;
import by.mashnyuk.hotels.model.dto.response.AddressDto;
import by.mashnyuk.hotels.model.dto.response.ArrivalTimeDto;
import by.mashnyuk.hotels.model.dto.response.ContactsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Hotel Integration Tests (HTTP & Database)")
class HotelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CreateHotelDto buildValidCreateHotelDto(String name) {
        return CreateHotelDto.builder()
                .name(name)
                .description("Luxury stay with panoramic city views")
                .brand("Hilton")
                .address(AddressDto.builder()
                        .houseNumber(9)
                        .street("Pobediteley Avenue")
                        .city("Minsk")
                        .country("Belarus")
                        .postCode("220004")
                        .build())
                .contacts(ContactsDto.builder()
                        .phone("+375 17 309-80-00")
                        .email("info@doubletree-minsk.by")
                        .build())
                .arrivalTime(ArrivalTimeDto.builder()
                        .checkIn("14:00")
                        .checkOut("12:00")
                        .build())
                .build();
    }

    @Nested
    @DisplayName("POST /property-view/hotels - Create Hotel")
    class CreateHotelIntegrationTests {

        @Test
        @DisplayName("Should persist new hotel and return 201 Created with short DTO summary")
        void shouldCreateHotelSuccessfully() throws Exception {
            CreateHotelDto requestBody = buildValidCreateHotelDto("DoubleTree by Hilton Minsk");

            mockMvc.perform(post("/property-view/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(greaterThan(0)))
                    .andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"))
                    .andExpect(jsonPath("$.phone").value("+375 17 309-80-00"))
                    .andExpect(jsonPath("$.address").value("9 Pobediteley Avenue, Minsk, 220004, Belarus"));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when mandatory DTO fields fail validation")
        void shouldReturn400WhenCreatePayloadInvalid() throws Exception {
            CreateHotelDto invalidDto = CreateHotelDto.builder()
                    .name("")
                    .build();

            mockMvc.perform(post("/property-view/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message", containsString("Validation failed")))
                    .andExpect(jsonPath("$.details.name").exists())
                    .andExpect(jsonPath("$.details.brand").exists())
                    .andExpect(jsonPath("$.details.address").exists())
                    .andExpect(jsonPath("$.details.contacts").exists())
                    .andExpect(jsonPath("$.details.arrivalTime").exists());
        }
    }

    @Nested
    @DisplayName("GET /property-view/hotels - Fetch All Hotels")
    class GetAllHotelsIntegrationTests {

        @Test
        @DisplayName("Should retrieve list of stored hotels containing newly created entry")
        void shouldReturnAllHotels() throws Exception {
            CreateHotelDto hotel1 = buildValidCreateHotelDto("Hotel Minsk");
            CreateHotelDto hotel2 = buildValidCreateHotelDto("Renaissance Minsk Hotel");

            mockMvc.perform(post("/property-view/hotels")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(hotel1)));

            mockMvc.perform(post("/property-view/hotels")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(hotel2)));

            mockMvc.perform(get("/property-view/hotels"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].name", containsInAnyOrder("Hotel Minsk", "Renaissance Minsk Hotel")));
        }
    }

    @Nested
    @DisplayName("GET /property-view/hotels/{id} - Get Hotel Details")
    class GetHotelByIdIntegrationTests {

        @Test
        @DisplayName("Should return 200 OK and full details for an existing hotel")
        void shouldReturnFullHotelDetails() throws Exception {
            CreateHotelDto request = buildValidCreateHotelDto("Minsk Marriott Hotel");

            MvcResult result = mockMvc.perform(post("/property-view/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            Long generatedId = objectMapper.readTree(responseBody).get("id").asLong();

            mockMvc.perform(get("/property-view/hotels/{id}", generatedId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(generatedId))
                    .andExpect(jsonPath("$.name").value("Minsk Marriott Hotel"))
                    .andExpect(jsonPath("$.brand").value("Hilton"));
        }

        @Test
        @DisplayName("Should return 404 Not Found for non-existent hotel ID")
        void shouldReturn404WhenHotelNotFound() throws Exception {
            long nonExistentId = 9999L;

            mockMvc.perform(get("/property-view/hotels/{id}", nonExistentId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("Hotel not found with id: " + nonExistentId))
                    .andExpect(jsonPath("$.path").value("uri=/property-view/hotels/" + nonExistentId));
        }
    }

    @Nested
    @DisplayName("POST /property-view/hotels/{id}/amenities - Add Amenities")
    class AddAmenitiesIntegrationTests {

        @Test
        @DisplayName("Should append amenities to hotel and persist state")
        void shouldAddAmenitiesToExistingHotel() throws Exception {
            CreateHotelDto request = buildValidCreateHotelDto("Boutique Hotel Hotel");

            MvcResult result = mockMvc.perform(post("/property-view/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            Long generatedId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
            List<String> amenities = List.of("Free Wi-Fi", "Swimming Pool", "Spa & Wellness");

            mockMvc.perform(post("/property-view/hotels/{id}/amenities", generatedId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(amenities)))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/property-view/hotels/{id}", generatedId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.amenities", hasSize(3)))
                    .andExpect(jsonPath("$.amenities", containsInAnyOrder("Free Wi-Fi", "Swimming Pool", "Spa & Wellness")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when adding amenities to missing hotel")
        void shouldReturn404WhenAddingAmenitiesToMissingHotel() throws Exception {
            List<String> amenities = List.of("Fitness Center");

            mockMvc.perform(post("/property-view/hotels/9999/amenities")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(amenities)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Hotel not found with id: 9999"));
        }
    }
}
