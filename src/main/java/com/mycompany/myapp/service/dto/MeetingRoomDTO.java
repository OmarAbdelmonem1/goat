package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MeetingRoom} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingRoomDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    @Min(value = 1)
    @Max(value = 500)
    private Integer capacity;

    @NotNull
    private Boolean requiresApproval;

    private Set<EquipmentDTO> equipment = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getRequiresApproval() {
        return requiresApproval;
    }

    public void setRequiresApproval(Boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }

    public Set<EquipmentDTO> getEquipment() {
        return equipment;
    }

    public void setEquipment(Set<EquipmentDTO> equipment) {
        this.equipment = equipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MeetingRoomDTO)) {
            return false;
        }

        MeetingRoomDTO meetingRoomDTO = (MeetingRoomDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, meetingRoomDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingRoomDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", capacity=" + getCapacity() +
            ", requiresApproval='" + getRequiresApproval() + "'" +
            ", equipment=" + getEquipment() +
            "}";
    }
}
