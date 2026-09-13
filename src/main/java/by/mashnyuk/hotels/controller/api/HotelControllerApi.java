package by.mashnyuk.hotels.controller.api;

import by.mashnyuk.hotels.model.dto.request.CreateHotelDto;
import by.mashnyuk.hotels.model.dto.response.ErrorResponse;
import by.mashnyuk.hotels.model.dto.response.HotelFullDto;
import by.mashnyuk.hotels.model.dto.response.HotelShortDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Tag(name = "Hotel Management", description = "API for managing hotel properties, searching, and viewing analytics")
@RequestMapping("/property-view")
public interface HotelControllerApi {

    @Operation(summary = "Get all hotels", description = "Retrieves a brief list of all registered hotels")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hotels list retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = HotelShortDto.class))
                    )
            )
    })
    @GetMapping("/hotels")
    ResponseEntity<List<HotelShortDto>> getHotels();

    @Operation(summary = "Get hotel by ID", description = "Retrieves detailed information about a specific hotel")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hotel details found and returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HotelFullDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hotel not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/hotels/{id}")
    ResponseEntity<HotelFullDto> getHotelById(@PathVariable Long id);

    @Operation(summary = "Create a new hotel", description = "Registers a new hotel property in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Hotel successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HotelShortDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid hotel input data provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/hotels")
    ResponseEntity<HotelShortDto> createHotel(@Valid @RequestBody CreateHotelDto createHotelDto);

    @Operation(summary = "Add amenities to hotel", description = "Attaches a list of amenities to a specific hotel")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Amenities successfully added"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hotel not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/hotels/{id}/amenities")
    void addAmenities(@PathVariable Long id, @RequestBody List<String> amenities);

    @Operation(summary = "Search hotels by criteria", description = "Searches hotels based on query parameters such as name, brand, city, country, or amenities")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hotel search completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = HotelShortDto.class))
                    )
            )
    })
    @GetMapping("/search")
    ResponseEntity<List<HotelShortDto>> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> amenities);

    @Operation(summary = "Get histogram data", description = "Generates a grouping histogram by a specified parameter (e.g. brand, city, country, amenities)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Histogram generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid histogram grouping parameter supplied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/histogram/{param}")
    ResponseEntity<Map<String, Long>> getHistogram(
            @Parameter(description = "Parameter to group histogram by (brand, city, country, amenities)")
            @PathVariable String param);
}