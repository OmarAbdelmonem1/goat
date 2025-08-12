package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.MeetingRoom;
import com.mycompany.myapp.repository.MeetingRoomRepository;
import com.mycompany.myapp.service.dto.MeetingRoomDTO;
import com.mycompany.myapp.service.mapper.MeetingRoomMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MeetingRoom}.
 */
@Service
@Transactional
public class MeetingRoomService {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingRoomService.class);

    private final MeetingRoomRepository meetingRoomRepository;

    private final MeetingRoomMapper meetingRoomMapper;

    public MeetingRoomService(MeetingRoomRepository meetingRoomRepository, MeetingRoomMapper meetingRoomMapper) {
        this.meetingRoomRepository = meetingRoomRepository;
        this.meetingRoomMapper = meetingRoomMapper;
    }

    /**
     * Save a meetingRoom.
     *
     * @param meetingRoomDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingRoomDTO save(MeetingRoomDTO meetingRoomDTO) {
        LOG.debug("Request to save MeetingRoom : {}", meetingRoomDTO);
        MeetingRoom meetingRoom = meetingRoomMapper.toEntity(meetingRoomDTO);
        meetingRoom = meetingRoomRepository.save(meetingRoom);
        return meetingRoomMapper.toDto(meetingRoom);
    }

    /**
     * Update a meetingRoom.
     *
     * @param meetingRoomDTO the entity to save.
     * @return the persisted entity.
     */
    public MeetingRoomDTO update(MeetingRoomDTO meetingRoomDTO) {
        LOG.debug("Request to update MeetingRoom : {}", meetingRoomDTO);
        MeetingRoom meetingRoom = meetingRoomMapper.toEntity(meetingRoomDTO);
        meetingRoom = meetingRoomRepository.save(meetingRoom);
        return meetingRoomMapper.toDto(meetingRoom);
    }

    /**
     * Partially update a meetingRoom.
     *
     * @param meetingRoomDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MeetingRoomDTO> partialUpdate(MeetingRoomDTO meetingRoomDTO) {
        LOG.debug("Request to partially update MeetingRoom : {}", meetingRoomDTO);

        return meetingRoomRepository
            .findById(meetingRoomDTO.getId())
            .map(existingMeetingRoom -> {
                meetingRoomMapper.partialUpdate(existingMeetingRoom, meetingRoomDTO);

                return existingMeetingRoom;
            })
            .map(meetingRoomRepository::save)
            .map(meetingRoomMapper::toDto);
    }

    /**
     * Get all the meetingRooms with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MeetingRoomDTO> findAllWithEagerRelationships(Pageable pageable) {
        return meetingRoomRepository.findAllWithEagerRelationships(pageable).map(meetingRoomMapper::toDto);
    }

    /**
     * Get one meetingRoom by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MeetingRoomDTO> findOne(Long id) {
        LOG.debug("Request to get MeetingRoom : {}", id);
        return meetingRoomRepository.findOneWithEagerRelationships(id).map(meetingRoomMapper::toDto);
    }

    /**
     * Delete the meetingRoom by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MeetingRoom : {}", id);
        meetingRoomRepository.deleteById(id);
    }
}
