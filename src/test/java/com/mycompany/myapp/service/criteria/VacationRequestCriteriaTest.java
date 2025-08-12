package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class VacationRequestCriteriaTest {

    @Test
    void newVacationRequestCriteriaHasAllFiltersNullTest() {
        var vacationRequestCriteria = new VacationRequestCriteria();
        assertThat(vacationRequestCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void vacationRequestCriteriaFluentMethodsCreatesFiltersTest() {
        var vacationRequestCriteria = new VacationRequestCriteria();

        setAllFilters(vacationRequestCriteria);

        assertThat(vacationRequestCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void vacationRequestCriteriaCopyCreatesNullFilterTest() {
        var vacationRequestCriteria = new VacationRequestCriteria();
        var copy = vacationRequestCriteria.copy();

        assertThat(vacationRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(vacationRequestCriteria)
        );
    }

    @Test
    void vacationRequestCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var vacationRequestCriteria = new VacationRequestCriteria();
        setAllFilters(vacationRequestCriteria);

        var copy = vacationRequestCriteria.copy();

        assertThat(vacationRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(vacationRequestCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var vacationRequestCriteria = new VacationRequestCriteria();

        assertThat(vacationRequestCriteria).hasToString("VacationRequestCriteria{}");
    }

    private static void setAllFilters(VacationRequestCriteria vacationRequestCriteria) {
        vacationRequestCriteria.id();
        vacationRequestCriteria.startDate();
        vacationRequestCriteria.endDate();
        vacationRequestCriteria.type();
        vacationRequestCriteria.status();
        vacationRequestCriteria.createdAt();
        vacationRequestCriteria.updatedAt();
        vacationRequestCriteria.attachmentsId();
        vacationRequestCriteria.employeeId();
        vacationRequestCriteria.distinct();
    }

    private static Condition<VacationRequestCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getAttachmentsId()) &&
                condition.apply(criteria.getEmployeeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<VacationRequestCriteria> copyFiltersAre(
        VacationRequestCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getAttachmentsId(), copy.getAttachmentsId()) &&
                condition.apply(criteria.getEmployeeId(), copy.getEmployeeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
