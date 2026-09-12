package by.mashnyuk.hotels.controller;

import by.mashnyuk.hotels.exceptions.GlobalExceptionHandler;
import by.mashnyuk.hotels.exceptions.HotelNotFoundException;
import by.mashnyuk.hotels.model.dto.request.CreateHotelDto;
import by.mashnyuk.hotels.model.dto.response.AddressDto;
import by.mashnyuk.hotels.model.dto.response.ArrivalTimeDto;
import by.mashnyuk.hotels.model.dto.response.ContactsDto;
import by.mashnyuk.hotels.model.dto.response.HotelFullDto;
import by.mashnyuk.hotels.model.dto.response.HotelShortDto;
import by.mashnyuk.hotels.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelController.class)
@Import(GlobalExceptionHandler.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private HotelService hotelService;

    private HotelShortDto sampleShortDto;
    private HotelFullDto sampleFullDto;

    @BeforeEach
    void setUp() {
        sampleShortDto = HotelShortDto.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .description("Luxurious hotel in city center")
                .address("9 Pobediteley Avenue, Minsk, 220004, Belarus")
                .phone("+375 17 309-80-00")
                .build();

        sampleFullDto = HotelFullDto.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .brand("Hilton")
                .amenities(List.of("Free WiFi", "Fitness center"))
                .build();
    }

    @Nested
    @DisplayName("GET /property-view/hotels")
    class GetHotelsTests {

        @Test
        @DisplayName("Should return 200 OK with list of short hotel DTOs")
        void shouldReturnAllHotels() throws Exception {
            when(hotelService.getAllHotels()).thenReturn(List.of(sampleShortDto));

            mockMvc.perform(get("/property-view/hotels"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"))
                    .andExpect(jsonPath("$[0].phone").value("+375 17 309-80-00"));

            verify(hotelService).getAllHotels();
        }

        @Test
        @DisplayName("Should return 500 Internal Server Error when unexpected runtime exception occurs")
        void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {
            when(hotelService.getAllHotels()).thenThrow(new RuntimeException("Database connection failure"));

            mockMvc.perform(get("/property-view/hotels"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"))
                    .andExpect(jsonPath("$.message").value("Internal server error"));

            verify(hotelService).getAllHotels();
        }
    }

    @Nested
    @DisplayName("GET /property-view/hotels/{id}")
    class GetHotelByIdTests {

        @Test
        @DisplayName("Should return 200 OK with hotel full details when found")
        void shouldReturnHotelById() throws Exception {
            when(hotelService.getHotelById(1L)).thenReturn(sampleFullDto);

            mockMvc.perform(get("/property-view/hotels/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"))
                    .andExpect(jsonPath("$.brand").value("Hilton"))
                    .andExpect(jsonPath("$.amenities[0]").value("Free WiFi"));

            verify(hotelService).getHotelById(1L);
        }

        @Test
        @DisplayName("Should return 404 Not Found when hotel does not exist")
        void shouldReturn404WhenHotelNotFound() throws Exception {
            when(hotelService.getHotelById(99L)).thenThrow(new HotelNotFoundException(99L));

            mockMvc.perform(get("/property-view/hotels/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("Hotel not found with id: 99"));

            verify(hotelService).getHotelById(99L);
        }
    }

    @Nested
    @DisplayName("POST /property-view/hotels")
    class CreateHotelTests {

        @Test
        @DisplayName("Should return 201 Created and short DTO when payload is valid")
        void shouldCreateHotel() throws Exception {
            CreateHotelDto createDto = CreateHotelDto.builder()
                    .name("DoubleTree by Hilton Minsk")
                    .description("Luxurious hotel in city center")
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
                            .email("doubletree@hilton.com")
                            .build())
                    .arrivalTime(ArrivalTimeDto.builder()
                            .checkIn("14:00")
                            .checkOut("12:00")
                            .build())
                    .build();

            when(hotelService.createHotel(any(CreateHotelDto.class))).thenReturn(sampleShortDto);

            mockMvc.perform(post("/property-view/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"));

            verify(hotelService).createHotel(any(CreateHotelDto.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body fails validation (@Valid)")
        void shouldReturn400WhenValidationFails() throws Exception {
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
                    .andExpect(jsonPath("$.details.name").value("Hotel name is required")); // <- Заменено с $.errors.name на $.details.name
        }

        @Test
        @DisplayName("Should return 400 Bad Request when JSON is unparseable or body is missing")
        void shouldReturn400WhenMalformedJson() throws Exception {
            String malformedJson = "{ \"name\": \"DoubleTree\", \"brand\": ";

            mockMvc.perform(post("/property-view/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(malformedJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Nested
    @DisplayName("POST /property-view/hotels/{id}/amenities")
    class AddAmenitiesTests {

        @Test
        @DisplayName("Should return 200 OK when amenities added successfully")
        void shouldAddAmenitiesToHotel() throws Exception {
            List<String> amenities = List.of("Free WiFi", "Swimming pool");
            doNothing().when(hotelService).addAmenitiesToHotel(eq(1L), eq(amenities));

            mockMvc.perform(post("/property-view/hotels/1/amenities")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(amenities)))
                    .andExpect(status().isOk());

            verify(hotelService).addAmenitiesToHotel(eq(1L), eq(amenities));
        }

        @Test
        @DisplayName("Should return 404 Not Found when target hotel does not exist")
        void shouldReturn404WhenAddingAmenitiesToMissingHotel() throws Exception {
            List<String> amenities = List.of("Free WiFi");
            doThrow(new HotelNotFoundException(99L))
                    .when(hotelService).addAmenitiesToHotel(eq(99L), eq(amenities));

            mockMvc.perform(post("/property-view/hotels/99/amenities")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(amenities)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when ConstraintViolationException is thrown")
        void shouldReturn400WhenConstraintViolationOccurs() throws Exception {
            List<String> amenities = List.of("");
            doThrow(new ConstraintViolationException("addAmenitiesToHotel.amenities[0]: must not be blank", new HashSet<>()))
                    .when(hotelService).addAmenitiesToHotel(eq(1L), eq(amenities));

            mockMvc.perform(post("/property-view/hotels/1/amenities")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(amenities)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message", containsString("addAmenitiesToHotel.amenities[0]")));
        }
    }
}