package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A MeetingRoom.
 */
@Entity
@Table(name = "meeting_room")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Min(value = 1)
    @Max(value = 500)
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull
    @Column(name = "requires_approval", nullable = false)
    private Boolean requiresApproval;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meetingRoom")
    @JsonIgnoreProperties(value = { "invitedUsers", "employee", "meetingRoom" }, allowSetters = true)
    private Set<BookingRequest> bookingRequests = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_meeting_room__equipment",
        joinColumns = @JoinColumn(name = "meeting_room_id"),
        inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    @JsonIgnoreProperties(value = { "meetingRooms" }, allowSetters = true)
    private Set<Equipment> equipment = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MeetingRoom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MeetingRoom name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public MeetingRoom capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getRequiresApproval() {
        return this.requiresApproval;
    }

    public MeetingRoom requiresApproval(Boolean requiresApproval) {
        this.setRequiresApproval(requiresApproval);
        return this;
    }

    public void setRequiresApproval(Boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }

    public Set<BookingRequest> getBookingRequests() {
        return this.bookingRequests;
    }

    public void setBookingRequests(Set<BookingRequest> bookingRequests) {
        if (this.bookingRequests != null) {
            this.bookingRequests.forEach(i -> i.setMeetingRoom(null));
        }
        if (bookingRequests != null) {
            bookingRequests.forEach(i -> i.setMeetingRoom(this));
        }
        this.bookingRequests = bookingRequests;
    }

    public MeetingRoom bookingRequests(Set<BookingRequest> bookingRequests) {
        this.setBookingRequests(bookingRequests);
        return this;
    }

    public MeetingRoom addBookingRequests(BookingRequest bookingRequest) {
        this.bookingRequests.add(bookingRequest);
        bookingRequest.setMeetingRoom(this);
        return this;
    }

    public MeetingRoom removeBookingRequests(BookingRequest bookingRequest) {
        this.bookingRequests.remove(bookingRequest);
        bookingRequest.setMeetingRoom(null);
        return this;
    }

    public Set<Equipment> getEquipment() {
        return this.equipment;
    }

    public void setEquipment(Set<Equipment> equipment) {
        this.equipment = equipment;
    }

    public MeetingRoom equipment(Set<Equipment> equipment) {
        this.setEquipment(equipment);
        return this;
    }

    public MeetingRoom addEquipment(Equipment equipment) {
        this.equipment.add(equipment);
        return this;
    }

    public MeetingRoom removeEquipment(Equipment equipment) {
        this.equipment.remove(equipment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MeetingRoom)) {
            return false;
        }
        return getId() != null && getId().equals(((MeetingRoom) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingRoom{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", capacity=" + getCapacity() +
            ", requiresApproval='" + getRequiresApproval() + "'" +
            "}";
    }
}
