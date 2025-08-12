package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookingRequestTestSamples.*;
import static com.mycompany.myapp.domain.EmployeeTestSamples.*;
import static com.mycompany.myapp.domain.VacationRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employee.class);
        Employee employee1 = getEmployeeSample1();
        Employee employee2 = new Employee();
        assertThat(employee1).isNotEqualTo(employee2);

        employee2.setId(employee1.getId());
        assertThat(employee1).isEqualTo(employee2);

        employee2 = getEmployeeSample2();
        assertThat(employee1).isNotEqualTo(employee2);
    }

    @Test
    void bookingRequestsTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        BookingRequest bookingRequestBack = getBookingRequestRandomSampleGenerator();

        employee.addBookingRequests(bookingRequestBack);
        assertThat(employee.getBookingRequests()).containsOnly(bookingRequestBack);
        assertThat(bookingRequestBack.getEmployee()).isEqualTo(employee);

        employee.removeBookingRequests(bookingRequestBack);
        assertThat(employee.getBookingRequests()).doesNotContain(bookingRequestBack);
        assertThat(bookingRequestBack.getEmployee()).isNull();

        employee.bookingRequests(new HashSet<>(Set.of(bookingRequestBack)));
        assertThat(employee.getBookingRequests()).containsOnly(bookingRequestBack);
        assertThat(bookingRequestBack.getEmployee()).isEqualTo(employee);

        employee.setBookingRequests(new HashSet<>());
        assertThat(employee.getBookingRequests()).doesNotContain(bookingRequestBack);
        assertThat(bookingRequestBack.getEmployee()).isNull();
    }

    @Test
    void vacationRequestsTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        VacationRequest vacationRequestBack = getVacationRequestRandomSampleGenerator();

        employee.addVacationRequests(vacationRequestBack);
        assertThat(employee.getVacationRequests()).containsOnly(vacationRequestBack);
        assertThat(vacationRequestBack.getEmployee()).isEqualTo(employee);

        employee.removeVacationRequests(vacationRequestBack);
        assertThat(employee.getVacationRequests()).doesNotContain(vacationRequestBack);
        assertThat(vacationRequestBack.getEmployee()).isNull();

        employee.vacationRequests(new HashSet<>(Set.of(vacationRequestBack)));
        assertThat(employee.getVacationRequests()).containsOnly(vacationRequestBack);
        assertThat(vacationRequestBack.getEmployee()).isEqualTo(employee);

        employee.setVacationRequests(new HashSet<>());
        assertThat(employee.getVacationRequests()).doesNotContain(vacationRequestBack);
        assertThat(vacationRequestBack.getEmployee()).isNull();
    }

    @Test
    void invitationsTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        BookingRequest bookingRequestBack = getBookingRequestRandomSampleGenerator();

        employee.addInvitations(bookingRequestBack);
        assertThat(employee.getInvitations()).containsOnly(bookingRequestBack);
        assertThat(bookingRequestBack.getInvitedUsers()).containsOnly(employee);

        employee.removeInvitations(bookingRequestBack);
        assertThat(employee.getInvitations()).doesNotContain(bookingRequestBack);
        assertThat(bookingRequestBack.getInvitedUsers()).doesNotContain(employee);

        employee.invitations(new HashSet<>(Set.of(bookingRequestBack)));
        assertThat(employee.getInvitations()).containsOnly(bookingRequestBack);
        assertThat(bookingRequestBack.getInvitedUsers()).containsOnly(employee);

        employee.setInvitations(new HashSet<>());
        assertThat(employee.getInvitations()).doesNotContain(bookingRequestBack);
        assertThat(bookingRequestBack.getInvitedUsers()).doesNotContain(employee);
    }
}
