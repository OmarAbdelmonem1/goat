package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.BookingRequest;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.MeetingRoom;
import com.mycompany.myapp.service.dto.BookingRequestDTO;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.service.dto.MeetingRoomDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookingRequest} and its DTO {@link BookingRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookingRequestMapper extends EntityMapper<BookingRequestDTO, BookingRequest> {
    @Mapping(target = "invitedUsers", source = "invitedUsers", qualifiedByName = "employeeBasicSet")
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeBasic")
    @Mapping(target = "meetingRoom", source = "meetingRoom", qualifiedByName = "meetingRoomBasic")
    BookingRequestDTO toDto(BookingRequest s);

    @Mapping(target = "removeInvitedUsers", ignore = true)
    BookingRequest toEntity(BookingRequestDTO bookingRequestDTO);

    // --- Employee mapping with id + name ---
    @Named("employeeBasic")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    EmployeeDTO toDtoEmployeeBasic(Employee employee);

    @Named("employeeBasicSet")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    default Set<EmployeeDTO> toDtoEmployeeBasicSet(Set<Employee> employees) {
        return employees.stream().map(this::toDtoEmployeeBasic).collect(Collectors.toSet());
    }

    // --- MeetingRoom mapping with id + name ---
    @Named("meetingRoomBasic")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MeetingRoomDTO toDtoMeetingRoomBasic(MeetingRoom meetingRoom);
}
