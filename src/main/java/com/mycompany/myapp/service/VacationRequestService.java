package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.VacationRequest;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.VacationRequestRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.VacationRequestDTO;
import com.mycompany.myapp.service.mapper.EmployeeMapper;
import com.mycompany.myapp.service.mapper.VacationRequestMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.VacationRequest}.
 */
@Service
@Transactional
public class VacationRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(VacationRequestService.class);

    private final VacationRequestRepository vacationRequestRepository;

    private final VacationRequestMapper vacationRequestMapper;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public VacationRequestService(
        VacationRequestRepository vacationRequestRepository,
        VacationRequestMapper vacationRequestMapper,
        EmployeeRepository employeeRepository,
        EmployeeMapper employeeMapper
    ) {
        this.vacationRequestRepository = vacationRequestRepository;
        this.vacationRequestMapper = vacationRequestMapper;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    /**
     * Save a vacationRequest.
     *
     * @param vacationRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public VacationRequestDTO save(VacationRequestDTO vacationRequestDTO) {
        LOG.debug("Request to save VacationRequest : {}", vacationRequestDTO);

        // Map DTO to entity
        VacationRequest vacationRequest = vacationRequestMapper.toEntity(vacationRequestDTO);

        // Always set status to PENDING
        vacationRequest.setStatus(Status.PENDING);

        vacationRequest.setCreatedAt(Instant.now());
        vacationRequest.setUpdatedAt(Instant.now());

        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException("Current user ID not found", "bookingRequest", "usernotfound"));

        Employee employee = employeeRepository
            .findById(userId)
            .orElseThrow(() -> new BadRequestAlertException("Employee not found for current user", "bookingRequest", "employeenotfound"));

        vacationRequest.setEmployee(employee); // assuming you have employeeId field

        // Save entity
        vacationRequest = vacationRequestRepository.save(vacationRequest);

        // Map entity back to DTO
        return vacationRequestMapper.toDto(vacationRequest);
    }

    /**
     * Update a vacationRequest.
     *
     * @param vacationRequestDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public VacationRequestDTO update(VacationRequestDTO vacationRequestDTO) {
        LOG.debug("Request to update VacationRequest : {}", vacationRequestDTO);

        VacationRequest vacationRequest = vacationRequestMapper.toEntity(vacationRequestDTO);

        // لو status بقت ACCEPTED
        if ("Approved".equalsIgnoreCase(vacationRequest.getStatus().toString())) {
            Long employeeId = vacationRequest.getEmployee() != null ? vacationRequest.getEmployee().getId() : null;

            if (employeeId != null) {
                Employee employee = employeeRepository
                    .findById(employeeId)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

                int days = (int) ChronoUnit.DAYS.between(vacationRequest.getStartDate(), vacationRequest.getEndDate()) + 1;

                if (employee.getVacationBalance() != null && employee.getVacationBalance() >= days) {
                    employee.setVacationBalance(employee.getVacationBalance() - days);
                } else {
                    throw new IllegalStateException("Not enough vacation balance");
                }

                employeeRepository.save(employee);
            }
        }

        vacationRequest = vacationRequestRepository.save(vacationRequest);
        return vacationRequestMapper.toDto(vacationRequest);
    }

    /**
     * Partially update a vacationRequest.
     *
     * @param vacationRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VacationRequestDTO> partialUpdate(VacationRequestDTO vacationRequestDTO) {
        LOG.debug("Request to partially update VacationRequest : {}", vacationRequestDTO);

        return vacationRequestRepository
            .findById(vacationRequestDTO.getId())
            .map(existingVacationRequest -> {
                vacationRequestMapper.partialUpdate(existingVacationRequest, vacationRequestDTO);

                return existingVacationRequest;
            })
            .map(vacationRequestRepository::save)
            .map(vacationRequestMapper::toDto);
    }

    /**
     * Get one vacationRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VacationRequestDTO> findOne(Long id) {
        LOG.debug("Request to get VacationRequest : {}", id);
        return vacationRequestRepository.findById(id).map(vacationRequestMapper::toDto);
    }

    /**
     * Delete the vacationRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete VacationRequest : {}", id);
        vacationRequestRepository.deleteById(id);
    }
}
