package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.DepartmentType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Employee} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmployeeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    @Size(max = 254)
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;

    @NotNull
    private DepartmentType userRole;

    @NotNull
    private Instant createdAt;

    @NotNull
    @Min(value = 0)
    private Integer vacationBalance;

    private UserDTO user;

    private Set<BookingRequestDTO> invitations = new HashSet<>();

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DepartmentType getUserRole() {
        return userRole;
    }

    public void setUserRole(DepartmentType userRole) {
        this.userRole = userRole;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getVacationBalance() {
        return vacationBalance;
    }

    public void setVacationBalance(Integer vacationBalance) {
        this.vacationBalance = vacationBalance;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<BookingRequestDTO> getInvitations() {
        return invitations;
    }

    public void setInvitations(Set<BookingRequestDTO> invitations) {
        this.invitations = invitations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmployeeDTO)) {
            return false;
        }

        EmployeeDTO employeeDTO = (EmployeeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, employeeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", userRole='" + getUserRole() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", vacationBalance=" + getVacationBalance() +
            ", user=" + getUser() +
            ", invitations=" + getInvitations() +
            "}";
    }
}
