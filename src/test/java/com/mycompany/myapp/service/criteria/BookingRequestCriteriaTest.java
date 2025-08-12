package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BookingRequestCriteriaTest {

    @Test
    void newBookingRequestCriteriaHasAllFiltersNullTest() {
        var bookingRequestCriteria = new BookingRequestCriteria();
        assertThat(bookingRequestCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void bookingRequestCriteriaFluentMethodsCreatesFiltersTest() {
        var bookingRequestCriteria = new BookingRequestCriteria();

        setAllFilters(bookingRequestCriteria);

        assertThat(bookingRequestCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void bookingRequestCriteriaCopyCreatesNullFilterTest() {
        var bookingRequestCriteria = new BookingRequestCriteria();
        var copy = bookingRequestCriteria.copy();

        assertThat(bookingRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(bookingRequestCriteria)
        );
    }

    @Test
    void bookingRequestCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var bookingRequestCriteria = new BookingRequestCriteria();
        setAllFilters(bookingRequestCriteria);

        var copy = bookingRequestCriteria.copy();

        assertThat(bookingRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(bookingRequestCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var bookingRequestCriteria = new BookingRequestCriteria();

        assertThat(bookingRequestCriteria).hasToString("BookingRequestCriteria{}");
    }

    private static void setAllFilters(BookingRequestCriteria bookingRequestCriteria) {
        bookingRequestCriteria.id();
        bookingRequestCriteria.startTime();
        bookingRequestCriteria.endTime();
        bookingRequestCriteria.status();
        bookingRequestCriteria.createdAt();
        bookingRequestCriteria.updatedAt();
        bookingRequestCriteria.purpose();
        bookingRequestCriteria.invitedUsersId();
        bookingRequestCriteria.employeeId();
        bookingRequestCriteria.meetingRoomId();
        bookingRequestCriteria.distinct();
    }

    private static Condition<BookingRequestCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStartTime()) &&
                condition.apply(criteria.getEndTime()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getPurpose()) &&
                condition.apply(criteria.getInvitedUsersId()) &&
                condition.apply(criteria.getEmployeeId()) &&
                condition.apply(criteria.getMeetingRoomId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BookingRequestCriteria> copyFiltersAre(
        BookingRequestCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStartTime(), copy.getStartTime()) &&
                condition.apply(criteria.getEndTime(), copy.getEndTime()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getPurpose(), copy.getPurpose()) &&
                condition.apply(criteria.getInvitedUsersId(), copy.getInvitedUsersId()) &&
                condition.apply(criteria.getEmployeeId(), copy.getEmployeeId()) &&
                condition.apply(criteria.getMeetingRoomId(), copy.getMeetingRoomId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
