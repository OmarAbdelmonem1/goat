package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A BookingRequest.
 */
@Entity
@Table(name = "booking_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookingRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Size(max = 500)
    @Column(name = "purpose", length = 500)
    private String purpose;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_booking_request__invited_users",
        joinColumns = @JoinColumn(name = "booking_request_id"),
        inverseJoinColumns = @JoinColumn(name = "invited_users_id")
    )
    @JsonIgnoreProperties(value = { "user", "bookingRequests", "vacationRequests", "invitations" }, allowSetters = true)
    private Set<Employee> invitedUsers = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "bookingRequests", "vacationRequests", "invitations" }, allowSetters = true)
    private Employee employee;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bookingRequests", "equipment" }, allowSetters = true)
    private MeetingRoom meetingRoom;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookingRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public BookingRequest startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public BookingRequest endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return this.status;
    }

    public BookingRequest status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public BookingRequest createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public BookingRequest updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPurpose() {
        return this.purpose;
    }

    public BookingRequest purpose(String purpose) {
        this.setPurpose(purpose);
        return this;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Set<Employee> getInvitedUsers() {
        return this.invitedUsers;
    }

    public void setInvitedUsers(Set<Employee> employees) {
        this.invitedUsers = employees;
    }

    public BookingRequest invitedUsers(Set<Employee> employees) {
        this.setInvitedUsers(employees);
        return this;
    }

    public BookingRequest addInvitedUsers(Employee employee) {
        this.invitedUsers.add(employee);
        return this;
    }

    public BookingRequest removeInvitedUsers(Employee employee) {
        this.invitedUsers.remove(employee);
        return this;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public BookingRequest employee(Employee employee) {
        this.setEmployee(employee);
        return this;
    }

    public MeetingRoom getMeetingRoom() {
        return this.meetingRoom;
    }

    public void setMeetingRoom(MeetingRoom meetingRoom) {
        this.meetingRoom = meetingRoom;
    }

    public BookingRequest meetingRoom(MeetingRoom meetingRoom) {
        this.setMeetingRoom(meetingRoom);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookingRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((BookingRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingRequest{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", purpose='" + getPurpose() + "'" +
            "}";
    }
}
