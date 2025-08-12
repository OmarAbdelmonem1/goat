package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.MeetingRoom} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.MeetingRoomResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /meeting-rooms?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MeetingRoomCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private IntegerFilter capacity;

    private BooleanFilter requiresApproval;

    private LongFilter bookingRequestsId;

    private LongFilter equipmentId;

    private Boolean distinct;

    public MeetingRoomCriteria() {}

    public MeetingRoomCriteria(MeetingRoomCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.capacity = other.optionalCapacity().map(IntegerFilter::copy).orElse(null);
        this.requiresApproval = other.optionalRequiresApproval().map(BooleanFilter::copy).orElse(null);
        this.bookingRequestsId = other.optionalBookingRequestsId().map(LongFilter::copy).orElse(null);
        this.equipmentId = other.optionalEquipmentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MeetingRoomCriteria copy() {
        return new MeetingRoomCriteria(this);
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

    public IntegerFilter getCapacity() {
        return capacity;
    }

    public Optional<IntegerFilter> optionalCapacity() {
        return Optional.ofNullable(capacity);
    }

    public IntegerFilter capacity() {
        if (capacity == null) {
            setCapacity(new IntegerFilter());
        }
        return capacity;
    }

    public void setCapacity(IntegerFilter capacity) {
        this.capacity = capacity;
    }

    public BooleanFilter getRequiresApproval() {
        return requiresApproval;
    }

    public Optional<BooleanFilter> optionalRequiresApproval() {
        return Optional.ofNullable(requiresApproval);
    }

    public BooleanFilter requiresApproval() {
        if (requiresApproval == null) {
            setRequiresApproval(new BooleanFilter());
        }
        return requiresApproval;
    }

    public void setRequiresApproval(BooleanFilter requiresApproval) {
        this.requiresApproval = requiresApproval;
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

    public LongFilter getEquipmentId() {
        return equipmentId;
    }

    public Optional<LongFilter> optionalEquipmentId() {
        return Optional.ofNullable(equipmentId);
    }

    public LongFilter equipmentId() {
        if (equipmentId == null) {
            setEquipmentId(new LongFilter());
        }
        return equipmentId;
    }

    public void setEquipmentId(LongFilter equipmentId) {
        this.equipmentId = equipmentId;
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
        final MeetingRoomCriteria that = (MeetingRoomCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(capacity, that.capacity) &&
            Objects.equals(requiresApproval, that.requiresApproval) &&
            Objects.equals(bookingRequestsId, that.bookingRequestsId) &&
            Objects.equals(equipmentId, that.equipmentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacity, requiresApproval, bookingRequestsId, equipmentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeetingRoomCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalCapacity().map(f -> "capacity=" + f + ", ").orElse("") +
            optionalRequiresApproval().map(f -> "requiresApproval=" + f + ", ").orElse("") +
            optionalBookingRequestsId().map(f -> "bookingRequestsId=" + f + ", ").orElse("") +
            optionalEquipmentId().map(f -> "equipmentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
