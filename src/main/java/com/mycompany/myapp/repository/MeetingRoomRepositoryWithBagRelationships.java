package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MeetingRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface MeetingRoomRepositoryWithBagRelationships {
    Optional<MeetingRoom> fetchBagRelationships(Optional<MeetingRoom> meetingRoom);

    List<MeetingRoom> fetchBagRelationships(List<MeetingRoom> meetingRooms);

    Page<MeetingRoom> fetchBagRelationships(Page<MeetingRoom> meetingRooms);
}
