package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MeetingRoomDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MeetingRoomDTO.class);
        MeetingRoomDTO meetingRoomDTO1 = new MeetingRoomDTO();
        meetingRoomDTO1.setId(1L);
        MeetingRoomDTO meetingRoomDTO2 = new MeetingRoomDTO();
        assertThat(meetingRoomDTO1).isNotEqualTo(meetingRoomDTO2);
        meetingRoomDTO2.setId(meetingRoomDTO1.getId());
        assertThat(meetingRoomDTO1).isEqualTo(meetingRoomDTO2);
        meetingRoomDTO2.setId(2L);
        assertThat(meetingRoomDTO1).isNotEqualTo(meetingRoomDTO2);
        meetingRoomDTO1.setId(null);
        assertThat(meetingRoomDTO1).isNotEqualTo(meetingRoomDTO2);
    }
}
