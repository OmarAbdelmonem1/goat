package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.DepartmentType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Employee} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.EmployeeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /employees?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmployeeCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DepartmentType
     */
    public static class DepartmentTypeFilter extends Filter<DepartmentType> {

        public DepartmentTypeFilter() {}

        public DepartmentTypeFilter(DepartmentTypeFilter filter) {
            super(filter);
        }

        @Override
        public DepartmentTypeFilter copy() {
            return new DepartmentTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter email;

    private DepartmentTypeFilter userRole;

    private InstantFilter createdAt;

    private IntegerFilter vacationBalance;

    private LongFilter userId;

    private LongFilter bookingRequestsId;

    private LongFilter vacationRequestsId;

    private LongFilter invitationsId;

    private Boolean distinct;

    public EmployeeCriteria() {}

    public EmployeeCriteria(EmployeeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.userRole = other.optionalUserRole().map(DepartmentTypeFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.vacationBalance = other.optionalVacationBalance().map(IntegerFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.bookingRequestsId = other.optionalBookingRequestsId().map(LongFilter::copy).orElse(null);
        this.vacationRequestsId = other.optionalVacationRequestsId().map(LongFilter::copy).orElse(null);
        this.invitationsId = other.optionalInvitationsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EmployeeCriteria copy() {
        return new EmployeeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public DepartmentTypeFilter getUserRole() {
        return userRole;
    }

    public Optional<DepartmentTypeFilter> optionalUserRole() {
        return Optional.ofNullable(userRole);
    }

    public DepartmentTypeFilter userRole() {
        if (userRole == null) {
            setUserRole(new DepartmentTypeFilter());
        }
        return userRole;
    }

    public void setUserRole(DepartmentTypeFilter userRole) {
        this.userRole = userRole;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public IntegerFilter getVacationBalance() {
        return vacationBalance;
    }

    public Optional<IntegerFilter> optionalVacationBalance() {
        return Optional.ofNullable(vacationBalance);
    }

    public IntegerFilter vacationBalance() {
        if (vacationBalance == null) {
            setVacationBalance(new IntegerFilter());
        }
        return vacationBalance;
    }

    public void setVacationBalance(IntegerFilter vacationBalance) {
        this.vacationBalance = vacationBalance;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getBookingRequestsId() {
        return bookingRequestsId;
    }

    public Optional<LongFilter> optionalBookingRequestsId() {
        return Optional.ofNullable(bookingRequestsId);
    }

    public LongFilter bookingRequestsId() {
        if (bookingRequestsId == null) {
            setBookingRequestsId(new LongFilter());
        }
        return bookingRequestsId;
    }

    public void setBookingRequestsId(LongFilter bookingRequestsId) {
        this.bookingRequestsId = bookingRequestsId;
    }

    public LongFilter getVacationRequestsId() {
        return vacationRequestsId;
    }

    public Optional<LongFilter> optionalVacationRequestsId() {
        return Optional.ofNullable(vacationRequestsId);
    }

    public LongFilter vacationRequestsId() {
        if (vacationRequestsId == null) {
            setVacationRequestsId(new LongFilter());
        }
        return vacationRequestsId;
    }

    public void setVacationRequestsId(LongFilter vacationRequestsId) {
        this.vacationRequestsId = vacationRequestsId;
    }

    public LongFilter getInvitationsId() {
        return invitationsId;
    }

    public Optional<LongFilter> optionalInvitationsId() {
        return Optional.ofNullable(invitationsId);
    }

    public LongFilter invitationsId() {
        if (invitationsId == null) {
            setInvitationsId(new LongFilter());
        }
        return invitationsId;
    }

    public void setInvitationsId(LongFilter invitationsId) {
        this.invitationsId = invitationsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeCriteria that = (EmployeeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(email, that.email) &&
            Objects.equals(userRole, that.userRole) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(vacationBalance, that.vacationBalance) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(bookingRequestsId, that.bookingRequestsId) &&
            Objects.equals(vacationRequestsId, that.vacationRequestsId) &&
            Objects.equals(invitationsId, that.invitationsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            email,
            userRole,
            createdAt,
            vacationBalance,
            userId,
            bookingRequestsId,
            vacationRequestsId,
            invitationsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalUserRole().map(f -> "userRole=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalVacationBalance().map(f -> "vacationBalance=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalBookingRequestsId().map(f -> "bookingRequestsId=" + f + ", ").orElse("") +
            optionalVacationRequestsId().map(f -> "vacationRequestsId=" + f + ", ").orElse("") +
            optionalInvitationsId().map(f -> "invitationsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
