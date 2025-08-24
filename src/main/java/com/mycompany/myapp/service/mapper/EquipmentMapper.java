package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Equipment;
import com.mycompany.myapp.domain.MeetingRoom;
import com.mycompany.myapp.service.dto.EquipmentDTO;
import com.mycompany.myapp.service.dto.MeetingRoomDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Equipment} and its DTO {@link EquipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipmentMapper extends EntityMapper<EquipmentDTO, Equipment> {
    @Mapping(target = "meetingRooms", source = "meetingRooms", qualifiedByName = "meetingRoomIdSet")
    EquipmentDTO toDto(Equipment s);

    @Mapping(target = "meetingRooms", ignore = true)
    @Mapping(target = "removeMeetingRooms", ignore = true)
    Equipment toEntity(EquipmentDTO equipmentDTO);

    @Named("meetingRoomId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MeetingRoomDTO toDtoMeetingRoomId(MeetingRoom meetingRoom);

    @Named("meetingRoomIdSet")
    default Set<MeetingRoomDTO> toDtoMeetingRoomIdSet(Set<MeetingRoom> meetingRoom) {
        return meetingRoom.stream().map(this::toDtoMeetingRoomId).collect(Collectors.toSet());
    }
}
