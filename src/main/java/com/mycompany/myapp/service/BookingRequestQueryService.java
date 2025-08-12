package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.BookingRequest;
import com.mycompany.myapp.repository.BookingRequestRepository;
import com.mycompany.myapp.service.criteria.BookingRequestCriteria;
import com.mycompany.myapp.service.dto.BookingRequestDTO;
import com.mycompany.myapp.service.mapper.BookingRequestMapper;
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
 * Service for executing complex queries for {@link BookingRequest} entities in the database.
 * The main input is a {@link BookingRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookingRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookingRequestQueryService extends QueryService<BookingRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(BookingRequestQueryService.class);

    private final BookingRequestRepository bookingRequestRepository;

    private final BookingRequestMapper bookingRequestMapper;

    public BookingRequestQueryService(BookingRequestRepository bookingRequestRepository, BookingRequestMapper bookingRequestMapper) {
        this.bookingRequestRepository = bookingRequestRepository;
        this.bookingRequestMapper = bookingRequestMapper;
    }

    /**
     * Return a {@link Page} of {@link BookingRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookingRequestDTO> findByCriteria(BookingRequestCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BookingRequest> specification = createSpecification(criteria);
        return bookingRequestRepository
            .fetchBagRelationships(bookingRequestRepository.findAll(specification, page))
            .map(bookingRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookingRequestCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BookingRequest> specification = createSpecification(criteria);
        return bookingRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link BookingRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BookingRequest> createSpecification(BookingRequestCriteria criteria) {
        Specification<BookingRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), BookingRequest_.id),
                buildRangeSpecification(criteria.getStartTime(), BookingRequest_.startTime),
                buildRangeSpecification(criteria.getEndTime(), BookingRequest_.endTime),
                buildSpecification(criteria.getStatus(), BookingRequest_.status),
                buildRangeSpecification(criteria.getCreatedAt(), BookingRequest_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), BookingRequest_.updatedAt),
                buildStringSpecification(criteria.getPurpose(), BookingRequest_.purpose),
                buildSpecification(criteria.getInvitedUsersId(), root ->
                    root.join(BookingRequest_.invitedUsers, JoinType.LEFT).get(Employee_.id)
                ),
                buildSpecification(criteria.getEmployeeId(), root -> root.join(BookingRequest_.employee, JoinType.LEFT).get(Employee_.id)),
                buildSpecification(criteria.getMeetingRoomId(), root ->
                    root.join(BookingRequest_.meetingRoom, JoinType.LEFT).get(MeetingRoom_.id)
                )
            );
        }
        return specification;
    }
}
