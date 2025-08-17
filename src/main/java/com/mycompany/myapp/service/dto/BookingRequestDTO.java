package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.Status;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.BookingRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookingRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    @NotNull
    private Status status;

    //@NotNull
    private Instant createdAt;

    private Instant updatedAt;

    @Size(max = 500)
    private String purpose;

    private Set<EmployeeDTO> invitedUsers = new HashSet<>();

    //@NotNull
    private EmployeeDTO employee;

    @NotNull
    private MeetingRoomDTO meetingRoom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Set<EmployeeDTO> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(Set<EmployeeDTO> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

    public MeetingRoomDTO getMeetingRoom() {
        return meetingRoom;
    }

    public void setMeetingRoom(MeetingRoomDTO meetingRoom) {
        this.meetingRoom = meetingRoom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookingRequestDTO)) {
            return false;
        }

        BookingRequestDTO bookingRequestDTO = (BookingRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookingRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingRequestDTO{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", purpose='" + getPurpose() + "'" +
            ", invitedUsers=" + getInvitedUsers() +
            ", employee=" + getEmployee() +
            ", meetingRoom=" + getMeetingRoom() +
            "}";
    }
}
