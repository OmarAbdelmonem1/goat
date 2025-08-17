package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Attachment;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.VacationRequest;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.AttachmentRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.VacationRequestRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.VacationRequestDTO;
import com.mycompany.myapp.service.mapper.EmployeeMapper;
import com.mycompany.myapp.service.mapper.VacationRequestMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Extended service for VacationRequest to handle additional queries like current user requests.
 */
@Service
public class VacationRequestServiceExtension extends VacationRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(VacationRequestServiceExtension.class);

    private final VacationRequestQueryService vacationRequestQueryService;
    private final AttachmentRepository attachmentRepository;

    public VacationRequestServiceExtension(
        VacationRequestRepository vacationRequestRepository,
        VacationRequestMapper vacationRequestMapper,
        EmployeeRepository employeeRepository,
        EmployeeMapper employeeMapper,
        VacationRequestQueryService vacationRequestQueryService,
        AttachmentRepository attachmentRepository
    ) {
        super(vacationRequestRepository, vacationRequestMapper, employeeRepository, employeeMapper);
        this.vacationRequestQueryService = vacationRequestQueryService;
        this.attachmentRepository = attachmentRepository;
    }

    /**
     * Save a vacationRequest with attachments
     */
    public VacationRequestDTO save(VacationRequestDTO dto, List<Attachment> attachments) {
        LOG.debug("Request to save VacationRequest : {}", dto);

        VacationRequest vacationRequestEntity = vacationRequestMapper.toEntity(dto);
        vacationRequestEntity.setStatus(Status.PENDING);
        vacationRequestEntity.setCreatedAt(Instant.now());
        vacationRequestEntity.setUpdatedAt(Instant.now());

        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException("Current user ID not found", "vacationRequest", "usernotfound"));

        Employee employee = employeeRepository
            .findById(userId)
            .orElseThrow(() -> new BadRequestAlertException("Employee not found", "vacationRequest", "employeenotfound"));
        vacationRequestEntity.setEmployee(employee);

        VacationRequest savedVacationRequest = vacationRequestRepository.save(vacationRequestEntity);
        // Save attachments
        if (attachments != null) {
            attachments.forEach(att -> {
                att.setVacationRequest(savedVacationRequest);
                att.setUploadedAt(Instant.now());
                attachmentRepository.save(att);
            });
        }

        return vacationRequestMapper.toDto(savedVacationRequest);
    }

    /**
     * Update a vacationRequest with attachments
     */
    @Transactional
    public VacationRequestDTO update(VacationRequestDTO dto, List<Attachment> attachments) {
        LOG.debug("Request to update VacationRequest : {}", dto);

        // Map DTO to entity
        VacationRequest vacationRequestEntity = vacationRequestMapper.toEntity(dto);

        // Handle vacation balance if approved
        if ("Approved".equalsIgnoreCase(vacationRequestEntity.getStatus().toString())) {
            Employee employee = vacationRequestEntity.getEmployee();
            if (employee != null) {
                int days = (int) ChronoUnit.DAYS.between(vacationRequestEntity.getStartDate(), vacationRequestEntity.getEndDate()) + 1;
                if (employee.getVacationBalance() != null && employee.getVacationBalance() >= days) {
                    employee.setVacationBalance(employee.getVacationBalance() - days);
                    employeeRepository.save(employee);
                } else {
                    throw new IllegalStateException("Not enough vacation balance");
                }
            }
        }

        // Save vacation request once
        VacationRequest savedVacationRequest = vacationRequestRepository.save(vacationRequestEntity);

        // Save/update attachments
        if (attachments != null) {
            attachments.forEach(att -> {
                att.setVacationRequest(savedVacationRequest); // use final variable
                att.setUploadedAt(Instant.now());
                attachmentRepository.save(att);
            });
        }

        return vacationRequestMapper.toDto(savedVacationRequest);
    }

    /**
     * Delete a vacationRequest along with attachments
     */
    public void delete(Long id) {
        LOG.debug("Request to delete VacationRequest : {}", id);

        vacationRequestRepository
            .findById(id)
            .ifPresent(vacationRequest -> {
                // Delete attachments first
                attachmentRepository.deleteAll(vacationRequest.getAttachments());
                // Then delete vacation request
                vacationRequestRepository.delete(vacationRequest);
            });
    }

    /**
     * Get all vacation requests for the currently authenticated user.
     *
     * @return a list of VacationRequestDTOs for the current user
     */
    @Transactional
    public List<VacationRequestDTO> findMyVacationRequests() {
        LOG.debug("Request to get VacationRequests for the current user");

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("User not authenticated", "vacationRequest", "usernotfound"));

        Employee employee = employeeRepository
            .findOneByUserLogin(currentUserLogin)
            .orElseThrow(() -> new BadRequestAlertException("Employee not found", "vacationRequest", "employeenotfound"));

        List<VacationRequest> vacationRequests = vacationRequestRepository.findByEmployeeId(employee.getId());

        LOG.debug("Found {} vacation requests for user {}", vacationRequests.size(), currentUserLogin);

        return vacationRequests.stream().map(vacationRequestMapper::toDto).toList();
    }

    /**
     * Optionally, extend this service to fetch vacation requests with attachments
     */
    @Transactional
    public List<VacationRequestDTO> findMyVacationRequestsWithAttachments() {
        LOG.debug("Request to get VacationRequests with attachments for the current user");

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("User not authenticated", "vacationRequest", "usernotfound"));

        Employee employee = employeeRepository
            .findOneByUserLogin(currentUserLogin)
            .orElseThrow(() -> new BadRequestAlertException("Employee not found", "vacationRequest", "employeenotfound"));

        List<VacationRequest> vacationRequests = vacationRequestRepository.findByEmployeeId(employee.getId());

        vacationRequests.forEach(vr -> vr.getAttachments().size()); // force loading attachments if LAZY

        LOG.debug("Found {} vacation requests with attachments for user {}", vacationRequests.size(), currentUserLogin);

        return vacationRequests.stream().map(vacationRequestMapper::toDto).toList();
    }
}
