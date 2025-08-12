package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.VacationRequest;
import com.mycompany.myapp.repository.VacationRequestRepository;
import com.mycompany.myapp.service.criteria.VacationRequestCriteria;
import com.mycompany.myapp.service.dto.VacationRequestDTO;
import com.mycompany.myapp.service.mapper.VacationRequestMapper;
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
 * Service for executing complex queries for {@link VacationRequest} entities in the database.
 * The main input is a {@link VacationRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VacationRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VacationRequestQueryService extends QueryService<VacationRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(VacationRequestQueryService.class);

    private final VacationRequestRepository vacationRequestRepository;

    private final VacationRequestMapper vacationRequestMapper;

    public VacationRequestQueryService(VacationRequestRepository vacationRequestRepository, VacationRequestMapper vacationRequestMapper) {
        this.vacationRequestRepository = vacationRequestRepository;
        this.vacationRequestMapper = vacationRequestMapper;
    }

    /**
     * Return a {@link Page} of {@link VacationRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VacationRequestDTO> findByCriteria(VacationRequestCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VacationRequest> specification = createSpecification(criteria);
        return vacationRequestRepository.findAll(specification, page).map(vacationRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VacationRequestCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<VacationRequest> specification = createSpecification(criteria);
        return vacationRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link VacationRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VacationRequest> createSpecification(VacationRequestCriteria criteria) {
        Specification<VacationRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), VacationRequest_.id),
                buildRangeSpecification(criteria.getStartDate(), VacationRequest_.startDate),
                buildRangeSpecification(criteria.getEndDate(), VacationRequest_.endDate),
                buildSpecification(criteria.getType(), VacationRequest_.type),
                buildSpecification(criteria.getStatus(), VacationRequest_.status),
                buildRangeSpecification(criteria.getCreatedAt(), VacationRequest_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), VacationRequest_.updatedAt),
                buildSpecification(criteria.getAttachmentsId(), root ->
                    root.join(VacationRequest_.attachments, JoinType.LEFT).get(Attachment_.id)
                ),
                buildSpecification(criteria.getEmployeeId(), root -> root.join(VacationRequest_.employee, JoinType.LEFT).get(Employee_.id))
            );
        }
        return specification;
    }
}
