package by.mashnyuk.hotels.mapper;

import by.mashnyuk.hotels.model.Address;
import by.mashnyuk.hotels.model.Amenity;
import by.mashnyuk.hotels.model.ArrivalTime;
import by.mashnyuk.hotels.model.Contacts;
import by.mashnyuk.hotels.model.Hotel;
import by.mashnyuk.hotels.model.dto.response.AddressDto;
import by.mashnyuk.hotels.model.dto.response.ArrivalTimeDto;
import by.mashnyuk.hotels.model.dto.response.ContactsDto;
import by.mashnyuk.hotels.model.dto.request.CreateHotelDto;
import by.mashnyuk.hotels.model.dto.response.HotelFullDto;
import by.mashnyuk.hotels.model.dto.response.HotelShortDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        builder = @Builder(disableBuilder = true)
)
public interface HotelMapper {

    @Mapping(target = "address", source = "address", qualifiedByName = "formatAddressString")
    @Mapping(target = "phone", source = "contacts.phone")
    HotelShortDto toShortDto(Hotel hotel);

    List<HotelShortDto> toShortDtoList(List<Hotel> hotels);

    @Mapping(target = "amenities", source = "amenities", qualifiedByName = "mapAmenitiesToStrings")
    HotelFullDto toFullDto(Hotel hotel);

    AddressDto toAddressDto(Address address);

    ContactsDto toContactsDto(Contacts contacts);

    ArrivalTimeDto toArrivalTimeDto(ArrivalTime arrivalTime);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    Hotel toEntity(CreateHotelDto dto);

    Address toAddressEntity(AddressDto dto);

    Contacts toContactsEntity(ContactsDto dto);

    ArrivalTime toArrivalTimeEntity(ArrivalTimeDto dto);

    @Named("formatAddressString")
    default String formatAddressString(Address address) {
        if (address == null) {
            return null;
        }
        return String.format("%d %s, %s, %s, %s",
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getPostCode(),
                address.getCountry());
    }

    @Named("mapAmenitiesToStrings")
    default List<String> mapAmenitiesToStrings(Collection<Amenity> amenities) {
        if (amenities == null) {
            return Collections.emptyList();
        }
        return amenities.stream()
                .map(Amenity::getName)
                .toList();
    }
}