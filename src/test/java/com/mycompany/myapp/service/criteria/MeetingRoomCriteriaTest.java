package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MeetingRoomCriteriaTest {

    @Test
    void newMeetingRoomCriteriaHasAllFiltersNullTest() {
        var meetingRoomCriteria = new MeetingRoomCriteria();
        assertThat(meetingRoomCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void meetingRoomCriteriaFluentMethodsCreatesFiltersTest() {
        var meetingRoomCriteria = new MeetingRoomCriteria();

        setAllFilters(meetingRoomCriteria);

        assertThat(meetingRoomCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void meetingRoomCriteriaCopyCreatesNullFilterTest() {
        var meetingRoomCriteria = new MeetingRoomCriteria();
        var copy = meetingRoomCriteria.copy();

        assertThat(meetingRoomCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(meetingRoomCriteria)
        );
    }

    @Test
    void meetingRoomCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var meetingRoomCriteria = new MeetingRoomCriteria();
        setAllFilters(meetingRoomCriteria);

        var copy = meetingRoomCriteria.copy();

        assertThat(meetingRoomCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(meetingRoomCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var meetingRoomCriteria = new MeetingRoomCriteria();

        assertThat(meetingRoomCriteria).hasToString("MeetingRoomCriteria{}");
    }

    private static void setAllFilters(MeetingRoomCriteria meetingRoomCriteria) {
        meetingRoomCriteria.id();
        meetingRoomCriteria.name();
        meetingRoomCriteria.capacity();
        meetingRoomCriteria.requiresApproval();
        meetingRoomCriteria.bookingRequestsId();
        meetingRoomCriteria.equipmentId();
        meetingRoomCriteria.distinct();
    }

    private static Condition<MeetingRoomCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCapacity()) &&
                condition.apply(criteria.getRequiresApproval()) &&
                condition.apply(criteria.getBookingRequestsId()) &&
                condition.apply(criteria.getEquipmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MeetingRoomCriteria> copyFiltersAre(MeetingRoomCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCapacity(), copy.getCapacity()) &&
                condition.apply(criteria.getRequiresApproval(), copy.getRequiresApproval()) &&
                condition.apply(criteria.getBookingRequestsId(), copy.getBookingRequestsId()) &&
                condition.apply(criteria.getEquipmentId(), copy.getEquipmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
