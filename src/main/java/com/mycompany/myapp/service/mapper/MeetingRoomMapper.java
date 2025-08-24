package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Equipment;
import com.mycompany.myapp.domain.MeetingRoom;
import com.mycompany.myapp.service.dto.EquipmentDTO;
import com.mycompany.myapp.service.dto.MeetingRoomDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MeetingRoom} and its DTO {@link MeetingRoomDTO}.
 */
@Mapper(componentModel = "spring")
public interface MeetingRoomMapper extends EntityMapper<MeetingRoomDTO, MeetingRoom> {
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentBasicSet")
    MeetingRoomDTO toDto(MeetingRoom s);

    @Mapping(target = "removeEquipment", ignore = true)
    MeetingRoom toEntity(MeetingRoomDTO meetingRoomDTO);

    // --- Equipment mapping with id + name ---
    @Named("equipmentBasic")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    EquipmentDTO toDtoEquipmentBasic(Equipment equipment);

    @Named("equipmentBasicSet")
    default Set<EquipmentDTO> toDtoEquipmentBasicSet(Set<Equipment> equipment) {
        return equipment.stream().map(this::toDtoEquipmentBasic).collect(Collectors.toSet());
    }
}
