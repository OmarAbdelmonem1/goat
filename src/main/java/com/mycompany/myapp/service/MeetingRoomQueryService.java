package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.MeetingRoom;
import com.mycompany.myapp.repository.MeetingRoomRepository;
import com.mycompany.myapp.service.criteria.MeetingRoomCriteria;
import com.mycompany.myapp.service.dto.MeetingRoomDTO;
import com.mycompany.myapp.service.mapper.MeetingRoomMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link MeetingRoom} entities in the database.
 * The main input is a {@link MeetingRoomCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link MeetingRoomDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MeetingRoomQueryService extends QueryService<MeetingRoom> {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingRoomQueryService.class);

    private final MeetingRoomRepository meetingRoomRepository;

    private final MeetingRoomMapper meetingRoomMapper;

    public MeetingRoomQueryService(MeetingRoomRepository meetingRoomRepository, MeetingRoomMapper meetingRoomMapper) {
        this.meetingRoomRepository = meetingRoomRepository;
        this.meetingRoomMapper = meetingRoomMapper;
    }

    /**
     * Return a {@link Page} of {@link MeetingRoomDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MeetingRoomDTO> findByCriteria(MeetingRoomCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MeetingRoom> specification = createSpecification(criteria);
        return meetingRoomRepository
            .fetchBagRelationships(meetingRoomRepository.findAll(specification, page))
            .map(meetingRoomMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MeetingRoomCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<MeetingRoom> specification = createSpecification(criteria);
        return meetingRoomRepository.count(specification);
    }

    /**
     * Function to convert {@link MeetingRoomCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MeetingRoom> createSpecification(MeetingRoomCriteria criteria) {
        Specification<MeetingRoom> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), MeetingRoom_.id),
                buildStringSpecification(criteria.getName(), MeetingRoom_.name),
                buildRangeSpecification(criteria.getCapacity(), MeetingRoom_.capacity),
                buildSpecification(criteria.getRequiresApproval(), MeetingRoom_.requiresApproval),
                buildSpecification(criteria.getBookingRequestsId(), root ->
                    root.join(MeetingRoom_.bookingRequests, JoinType.LEFT).get(BookingRequest_.id)
                ),
                buildSpecification(criteria.getEquipmentId(), root -> root.join(MeetingRoom_.equipment, JoinType.LEFT).get(Equipment_.id))
            );
        }
        return specification;
    }
}
