package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookingRequestTestSamples.*;
import static com.mycompany.myapp.domain.EquipmentTestSamples.*;
import static com.mycompany.myapp.domain.MeetingRoomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MeetingRoomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MeetingRoom.class);
        MeetingRoom meetingRoom1 = getMeetingRoomSample1();
        MeetingRoom meetingRoom2 = new MeetingRoom();
        assertThat(meetingRoom1).isNotEqualTo(meetingRoom2);

        meetingRoom2.setId(meetingRoom1.getId());
        assertThat(meetingRoom1).isEqualTo(meetingRoom2);

        meetingRoom2 = getMeetingRoomSample2();
        assertThat(meetingRoom1).isNotEqualTo(meetingRoom2);
    }

    @Test
    void bookingRequestsTest() {
        MeetingRoom meetingRoom = getMeetingRoomRandomSampleGenerator();
        BookingRequest bookingRequestBack = getBookingRequestRandomSampleGenerator();

        meetingRoom.addBookingRequests(bookingRequestBack);
        assertThat(meetingRoom.getBookingRequests()).containsOnly(bookingRequestBack);
        assertThat(bookingRequestBack.getMeetingRoom()).isEqualTo(meetingRoom);

        meetingRoom.removeBookingRequests(bookingRequestBack);
        assertThat(meetingRoom.getBookingRequests()).doesNotContain(bookingRequestBack);
        assertThat(bookingRequestBack.getMeetingRoom()).isNull();

        meetingRoom.bookingRequests(new HashSet<>(Set.of(bookingRequestBack)));
        assertThat(meetingRoom.getBookingRequests()).containsOnly(bookingRequestBack);
        assertThat(bookingRequestBack.getMeetingRoom()).isEqualTo(meetingRoom);

        meetingRoom.setBookingRequests(new HashSet<>());
        assertThat(meetingRoom.getBookingRequests()).doesNotContain(bookingRequestBack);
        assertThat(bookingRequestBack.getMeetingRoom()).isNull();
    }

    @Test
    void equipmentTest() {
        MeetingRoom meetingRoom = getMeetingRoomRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        meetingRoom.addEquipment(equipmentBack);
        assertThat(meetingRoom.getEquipment()).containsOnly(equipmentBack);

        meetingRoom.removeEquipment(equipmentBack);
        assertThat(meetingRoom.getEquipment()).doesNotContain(equipmentBack);

        meetingRoom.equipment(new HashSet<>(Set.of(equipmentBack)));
        assertThat(meetingRoom.getEquipment()).containsOnly(equipmentBack);

        meetingRoom.setEquipment(new HashSet<>());
        assertThat(meetingRoom.getEquipment()).doesNotContain(equipmentBack);
    }
}
