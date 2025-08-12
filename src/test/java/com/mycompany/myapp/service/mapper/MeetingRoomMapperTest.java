package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MeetingRoomAsserts.*;
import static com.mycompany.myapp.domain.MeetingRoomTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MeetingRoomMapperTest {

    private MeetingRoomMapper meetingRoomMapper;

    @BeforeEach
    void setUp() {
        meetingRoomMapper = new MeetingRoomMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMeetingRoomSample1();
        var actual = meetingRoomMapper.toEntity(meetingRoomMapper.toDto(expected));
        assertMeetingRoomAllPropertiesEquals(expected, actual);
    }
}
