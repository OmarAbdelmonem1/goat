package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MeetingRoom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class MeetingRoomRepositoryWithBagRelationshipsImpl implements MeetingRoomRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String MEETINGROOMS_PARAMETER = "meetingRooms";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<MeetingRoom> fetchBagRelationships(Optional<MeetingRoom> meetingRoom) {
        return meetingRoom.map(this::fetchEquipment);
    }

    @Override
    public Page<MeetingRoom> fetchBagRelationships(Page<MeetingRoom> meetingRooms) {
        return new PageImpl<>(
            fetchBagRelationships(meetingRooms.getContent()),
            meetingRooms.getPageable(),
            meetingRooms.getTotalElements()
        );
    }

    @Override
    public List<MeetingRoom> fetchBagRelationships(List<MeetingRoom> meetingRooms) {
        return Optional.of(meetingRooms).map(this::fetchEquipment).orElse(Collections.emptyList());
    }

    MeetingRoom fetchEquipment(MeetingRoom result) {
        return entityManager
            .createQuery(
                "select meetingRoom from MeetingRoom meetingRoom left join fetch meetingRoom.equipment where meetingRoom.id = :id",
                MeetingRoom.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<MeetingRoom> fetchEquipment(List<MeetingRoom> meetingRooms) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, meetingRooms.size()).forEach(index -> order.put(meetingRooms.get(index).getId(), index));
        List<MeetingRoom> result = entityManager
            .createQuery(
                "select meetingRoom from MeetingRoom meetingRoom left join fetch meetingRoom.equipment where meetingRoom in :meetingRooms",
                MeetingRoom.class
            )
            .setParameter(MEETINGROOMS_PARAMETER, meetingRooms)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
