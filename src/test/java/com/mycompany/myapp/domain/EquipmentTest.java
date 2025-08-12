package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EquipmentTestSamples.*;
import static com.mycompany.myapp.domain.MeetingRoomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EquipmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Equipment.class);
        Equipment equipment1 = getEquipmentSample1();
        Equipment equipment2 = new Equipment();
        assertThat(equipment1).isNotEqualTo(equipment2);

        equipment2.setId(equipment1.getId());
        assertThat(equipment1).isEqualTo(equipment2);

        equipment2 = getEquipmentSample2();
        assertThat(equipment1).isNotEqualTo(equipment2);
    }

    @Test
    void meetingRoomsTest() {
        Equipment equipment = getEquipmentRandomSampleGenerator();
        MeetingRoom meetingRoomBack = getMeetingRoomRandomSampleGenerator();

        equipment.addMeetingRooms(meetingRoomBack);
        assertThat(equipment.getMeetingRooms()).containsOnly(meetingRoomBack);
        assertThat(meetingRoomBack.getEquipment()).containsOnly(equipment);

        equipment.removeMeetingRooms(meetingRoomBack);
        assertThat(equipment.getMeetingRooms()).doesNotContain(meetingRoomBack);
        assertThat(meetingRoomBack.getEquipment()).doesNotContain(equipment);

        equipment.meetingRooms(new HashSet<>(Set.of(meetingRoomBack)));
        assertThat(equipment.getMeetingRooms()).containsOnly(meetingRoomBack);
        assertThat(meetingRoomBack.getEquipment()).containsOnly(equipment);

        equipment.setMeetingRooms(new HashSet<>());
        assertThat(equipment.getMeetingRooms()).doesNotContain(meetingRoomBack);
        assertThat(meetingRoomBack.getEquipment()).doesNotContain(equipment);
    }
}
