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
    @Mapping(target = "invitedUsers", source = "invitedUsers", qualifiedByName = "employeeIdSet")
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeId")
    @Mapping(target = "meetingRoom", source = "meetingRoom", qualifiedByName = "meetingRoomId")
    BookingRequestDTO toDto(BookingRequest s);

    @Mapping(target = "removeInvitedUsers", ignore = true)
    BookingRequest toEntity(BookingRequestDTO bookingRequestDTO);

    @Named("employeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EmployeeDTO toDtoEmployeeId(Employee employee);

    @Named("employeeIdSet")
    default Set<EmployeeDTO> toDtoEmployeeIdSet(Set<Employee> employee) {
        return employee.stream().map(this::toDtoEmployeeId).collect(Collectors.toSet());
    }

    @Named("meetingRoomId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MeetingRoomDTO toDtoMeetingRoomId(MeetingRoom meetingRoom);
}
