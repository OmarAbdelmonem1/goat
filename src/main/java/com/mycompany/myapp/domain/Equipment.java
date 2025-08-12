package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Equipment.
 */
@Entity
@Table(name = "equipment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Equipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "equipment")
    @JsonIgnoreProperties(value = { "bookingRequests", "equipment" }, allowSetters = true)
    private Set<MeetingRoom> meetingRooms = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Equipment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Equipment name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Equipment description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsAvailable() {
        return this.isAvailable;
    }

    public Equipment isAvailable(Boolean isAvailable) {
        this.setIsAvailable(isAvailable);
        return this;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Set<MeetingRoom> getMeetingRooms() {
        return this.meetingRooms;
    }

    public void setMeetingRooms(Set<MeetingRoom> meetingRooms) {
        if (this.meetingRooms != null) {
            this.meetingRooms.forEach(i -> i.removeEquipment(this));
        }
        if (meetingRooms != null) {
            meetingRooms.forEach(i -> i.addEquipment(this));
        }
        this.meetingRooms = meetingRooms;
    }

    public Equipment meetingRooms(Set<MeetingRoom> meetingRooms) {
        this.setMeetingRooms(meetingRooms);
        return this;
    }

    public Equipment addMeetingRooms(MeetingRoom meetingRoom) {
        this.meetingRooms.add(meetingRoom);
        meetingRoom.getEquipment().add(this);
        return this;
    }

    public Equipment removeMeetingRooms(MeetingRoom meetingRoom) {
        this.meetingRooms.remove(meetingRoom);
        meetingRoom.getEquipment().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipment)) {
            return false;
        }
        return getId() != null && getId().equals(((Equipment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Equipment{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", isAvailable='" + getIsAvailable() + "'" +
            "}";
    }
}
