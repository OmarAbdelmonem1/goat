package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookingRequestTestSamples.*;
import static com.mycompany.myapp.domain.EmployeeTestSamples.*;
import static com.mycompany.myapp.domain.MeetingRoomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BookingRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookingRequest.class);
        BookingRequest bookingRequest1 = getBookingRequestSample1();
        BookingRequest bookingRequest2 = new BookingRequest();
        assertThat(bookingRequest1).isNotEqualTo(bookingRequest2);

        bookingRequest2.setId(bookingRequest1.getId());
        assertThat(bookingRequest1).isEqualTo(bookingRequest2);

        bookingRequest2 = getBookingRequestSample2();
        assertThat(bookingRequest1).isNotEqualTo(bookingRequest2);
    }

    @Test
    void invitedUsersTest() {
        BookingRequest bookingRequest = getBookingRequestRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        bookingRequest.addInvitedUsers(employeeBack);
        assertThat(bookingRequest.getInvitedUsers()).containsOnly(employeeBack);

        bookingRequest.removeInvitedUsers(employeeBack);
        assertThat(bookingRequest.getInvitedUsers()).doesNotContain(employeeBack);

        bookingRequest.invitedUsers(new HashSet<>(Set.of(employeeBack)));
        assertThat(bookingRequest.getInvitedUsers()).containsOnly(employeeBack);

        bookingRequest.setInvitedUsers(new HashSet<>());
        assertThat(bookingRequest.getInvitedUsers()).doesNotContain(employeeBack);
    }

    @Test
    void employeeTest() {
        BookingRequest bookingRequest = getBookingRequestRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        bookingRequest.setEmployee(employeeBack);
        assertThat(bookingRequest.getEmployee()).isEqualTo(employeeBack);

        bookingRequest.employee(null);
        assertThat(bookingRequest.getEmployee()).isNull();
    }

    @Test
    void meetingRoomTest() {
        BookingRequest bookingRequest = getBookingRequestRandomSampleGenerator();
        MeetingRoom meetingRoomBack = getMeetingRoomRandomSampleGenerator();

        bookingRequest.setMeetingRoom(meetingRoomBack);
        assertThat(bookingRequest.getMeetingRoom()).isEqualTo(meetingRoomBack);

        bookingRequest.meetingRoom(null);
        assertThat(bookingRequest.getMeetingRoom()).isNull();
    }
}
