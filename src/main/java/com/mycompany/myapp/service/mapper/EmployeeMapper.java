package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.BookingRequest;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.BookingRequestDTO;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Employee} and its DTO {@link EmployeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "invitations", source = "invitations", qualifiedByName = "bookingRequestIdSet")
    EmployeeDTO toDto(Employee s);

    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "removeInvitations", ignore = true)
    Employee toEntity(EmployeeDTO employeeDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("bookingRequestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookingRequestDTO toDtoBookingRequestId(BookingRequest bookingRequest);

    @Named("bookingRequestIdSet")
    default Set<BookingRequestDTO> toDtoBookingRequestIdSet(Set<BookingRequest> bookingRequest) {
        return bookingRequest.stream().map(this::toDtoBookingRequestId).collect(Collectors.toSet());
    }
}
