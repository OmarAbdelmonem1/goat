package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.DepartmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Employee.
 */
@Entity
@Table(name = "employee")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Employee implements Serializable {

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
    @Size(max = 254)
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private DepartmentType userRole;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Min(value = 0)
    @Column(name = "vacation_balance", nullable = false)
    private Integer vacationBalance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true) // FK column
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    @JsonIgnoreProperties(value = { "invitedUsers", "employee", "meetingRoom" }, allowSetters = true)
    private Set<BookingRequest> bookingRequests = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    @JsonIgnoreProperties(value = { "attachments", "employee" }, allowSetters = true)
    private Set<VacationRequest> vacationRequests = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "invitedUsers")
    @JsonIgnoreProperties(value = { "invitedUsers", "employee", "meetingRoom" }, allowSetters = true)
    private Set<BookingRequest> invitations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Employee id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Employee name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public Employee email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DepartmentType getUserRole() {
        return this.userRole;
    }

    public Employee userRole(DepartmentType userRole) {
        this.setUserRole(userRole);
        return this;
    }

    public void setUserRole(DepartmentType userRole) {
        this.userRole = userRole;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Employee createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getVacationBalance() {
        return this.vacationBalance;
    }

    public Employee vacationBalance(Integer vacationBalance) {
        this.setVacationBalance(vacationBalance);
        return this;
    }

    public void setVacationBalance(Integer vacationBalance) {
        this.vacationBalance = vacationBalance;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Employee user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<BookingRequest> getBookingRequests() {
        return this.bookingRequests;
    }

    public void setBookingRequests(Set<BookingRequest> bookingRequests) {
        if (this.bookingRequests != null) {
            this.bookingRequests.forEach(i -> i.setEmployee(null));
        }
        if (bookingRequests != null) {
            bookingRequests.forEach(i -> i.setEmployee(this));
        }
        this.bookingRequests = bookingRequests;
    }

    public Employee bookingRequests(Set<BookingRequest> bookingRequests) {
        this.setBookingRequests(bookingRequests);
        return this;
    }

    public Employee addBookingRequests(BookingRequest bookingRequest) {
        this.bookingRequests.add(bookingRequest);
        bookingRequest.setEmployee(this);
        return this;
    }

    public Employee removeBookingRequests(BookingRequest bookingRequest) {
        this.bookingRequests.remove(bookingRequest);
        bookingRequest.setEmployee(null);
        return this;
    }

    public Set<VacationRequest> getVacationRequests() {
        return this.vacationRequests;
    }

    public void setVacationRequests(Set<VacationRequest> vacationRequests) {
        if (this.vacationRequests != null) {
            this.vacationRequests.forEach(i -> i.setEmployee(null));
        }
        if (vacationRequests != null) {
            vacationRequests.forEach(i -> i.setEmployee(this));
        }
        this.vacationRequests = vacationRequests;
    }

    public Employee vacationRequests(Set<VacationRequest> vacationRequests) {
        this.setVacationRequests(vacationRequests);
        return this;
    }

    public Employee addVacationRequests(VacationRequest vacationRequest) {
        this.vacationRequests.add(vacationRequest);
        vacationRequest.setEmployee(this);
        return this;
    }

    public Employee removeVacationRequests(VacationRequest vacationRequest) {
        this.vacationRequests.remove(vacationRequest);
        vacationRequest.setEmployee(null);
        return this;
    }

    public Set<BookingRequest> getInvitations() {
        return this.invitations;
    }

    public void setInvitations(Set<BookingRequest> bookingRequests) {
        if (this.invitations != null) {
            this.invitations.forEach(i -> i.removeInvitedUsers(this));
        }
        if (bookingRequests != null) {
            bookingRequests.forEach(i -> i.addInvitedUsers(this));
        }
        this.invitations = bookingRequests;
    }

    public Employee invitations(Set<BookingRequest> bookingRequests) {
        this.setInvitations(bookingRequests);
        return this;
    }

    public Employee addInvitations(BookingRequest bookingRequest) {
        this.invitations.add(bookingRequest);
        bookingRequest.getInvitedUsers().add(this);
        return this;
    }

    public Employee removeInvitations(BookingRequest bookingRequest) {
        this.invitations.remove(bookingRequest);
        bookingRequest.getInvitedUsers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        return getId() != null && getId().equals(((Employee) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Employee{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", userRole='" + getUserRole() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", vacationBalance=" + getVacationBalance() +
            "}";
    }
}
