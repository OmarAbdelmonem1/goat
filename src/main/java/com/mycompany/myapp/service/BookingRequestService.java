package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.BookingRequest;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.MeetingRoom;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.BookingRequestRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.MeetingRoomRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.BookingRequestDTO;
import com.mycompany.myapp.service.mapper.BookingRequestMapper;
import com.mycompany.myapp.service.mapper.EmployeeMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.checkerframework.checker.units.qual.t;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.BookingRequest}.
 */
@Service
@Transactional
public class BookingRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingRequestService.class);

    @Value("${spring.mail.username:noreply@localhost}")
    private String fromEmail;

    @Value("${application.name:Booking System}")
    private String applicationName;

    @Value("${jhipster.mail.from:noreply@localhost}")
    private String jhipsterFromEmail;

    private final BookingRequestRepository bookingRequestRepository;
    private final BookingRequestMapper bookingRequestMapper;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final MeetingRoomRepository meetingRoomRepository;
    private final MailService mailService;

    public BookingRequestService(
        BookingRequestRepository bookingRequestRepository,
        BookingRequestMapper bookingRequestMapper,
        EmployeeRepository employeeRepository,
        EmployeeMapper employeeMapper,
        MeetingRoomRepository meetingRoomRepository,
        MailService mailService
    ) {
        this.bookingRequestRepository = bookingRequestRepository;
        this.bookingRequestMapper = bookingRequestMapper;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.meetingRoomRepository = meetingRoomRepository;
        this.mailService = mailService;
    }

    /**
     * Save a bookingRequest.
     *
     * @param bookingRequestDTO the entity to save.
     * @return the persisted entity.
     */

    @Transactional
    public BookingRequestDTO save(BookingRequestDTO bookingRequestDTO) {
        LOG.debug("Creating BookingRequest for current user: {}", bookingRequestDTO);

        bookingRequestDTO.setCreatedAt(Instant.now());

        // Get current user
        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException("Current user ID not found", "bookingRequest", "usernotfound"));

        Employee employee = employeeRepository
            .findById(userId)
            .orElseThrow(() -> new BadRequestAlertException("Employee not found for current user", "bookingRequest", "employeenotfound"));

        bookingRequestDTO.setEmployee(employeeMapper.toDto(employee));

        // Get meeting room
        Long roomId = bookingRequestDTO.getMeetingRoom() != null ? bookingRequestDTO.getMeetingRoom().getId() : null;
        if (roomId == null) {
            throw new BadRequestAlertException("Meeting room is required", "bookingRequest", "roomnotfound");
        }

        MeetingRoom meetingRoom = meetingRoomRepository
            .findById(roomId)
            .orElseThrow(() -> new BadRequestAlertException("Meeting room not found", "bookingRequest", "roomnotfound"));

        // Check time overlapping for same room
        boolean hasOverlap = bookingRequestRepository.existsByMeetingRoomIdAndTimeOverlap(
            roomId,
            bookingRequestDTO.getStartTime(),
            bookingRequestDTO.getEndTime()
        );

        if (hasOverlap) {
            throw new BadRequestAlertException("error.timeoverlap", "bookingRequest", "timeoverlap");
        }

        // Set status based on requiresApproval
        if (Boolean.TRUE.equals(meetingRoom.getRequiresApproval())) {
            bookingRequestDTO.setStatus(Status.PENDING);
        } else {
            bookingRequestDTO.setStatus(Status.APPROVED);
        }

        // Save booking request
        BookingRequest entity = bookingRequestMapper.toEntity(bookingRequestDTO);
        entity = bookingRequestRepository.save(entity);
        //Send confirmation email for auto-approved bookings or pending requests
        try {
            if (entity.getStatus() == Status.APPROVED) {
                sendNotificationEmails(entity, true);
            } else if (entity.getStatus() == Status.PENDING) {
                sendNotificationEmails(entity, false);
            }
        } catch (Exception e) {
            LOG.error("Failed to send confirmation email for booking {}: {}", entity.getId(), e.getMessage());
            // Don't throw exception as booking was successful, just log the email error
        }

        return bookingRequestMapper.toDto(entity);
    }

    /**
     * Update a bookingRequest.
     *
     * @param bookingRequestDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public BookingRequestDTO update(BookingRequestDTO bookingRequestDTO) {
        LOG.debug("Request to update BookingRequest : {}", bookingRequestDTO);

        // Fetch existing entity
        BookingRequest existing = bookingRequestRepository
            .findById(bookingRequestDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Booking not found", "bookingRequest", "idnotfound"));

        // 🔹 Update fields from DTO
        existing.setStartTime(bookingRequestDTO.getStartTime());
        existing.setEndTime(bookingRequestDTO.getEndTime());
        existing.setPurpose(bookingRequestDTO.getPurpose());
        existing.setStatus(bookingRequestDTO.getStatus());
        existing.setUpdatedAt(bookingRequestDTO.getUpdatedAt());

        // Link employee from DTO if present
        if (bookingRequestDTO.getEmployee() != null && bookingRequestDTO.getEmployee().getId() != null) {
            Employee employee = employeeRepository
                .findById(bookingRequestDTO.getEmployee().getId())
                .orElseThrow(() -> new BadRequestAlertException("Employee not found", "bookingRequest", "employeenotfound"));
            existing.setEmployee(employee);
        }

        // Link meeting room if present
        if (bookingRequestDTO.getMeetingRoom() != null && bookingRequestDTO.getMeetingRoom().getId() != null) {
            MeetingRoom room = meetingRoomRepository
                .findById(bookingRequestDTO.getMeetingRoom().getId())
                .orElseThrow(() -> new BadRequestAlertException("Meeting room not found", "bookingRequest", "roomnotfound"));
            existing.setMeetingRoom(room);
        }

        // Save entity
        BookingRequest saved = bookingRequestRepository.save(existing);

        // 🔹 Send email if status changed
        if (saved.getStatus() == Status.APPROVED) {
            sendNotificationEmails(saved, true);
        } else if (saved.getStatus() == Status.REJECTED) {
            sendNotificationEmails(saved, false);
        }

        return bookingRequestMapper.toDto(saved);
    }

    private void sendNotificationEmails(BookingRequest request, boolean approved) {
        String subject = approved ? "Booking Approved ✅" : "Booking Rejected ❌";
        String message = approved ? "Your booking request has been approved." : "Your booking request has been rejected.";

        // Send to employee
        if (request.getEmployee() != null && request.getEmployee().getEmail() != null) {
            mailService.sendEmail(request.getEmployee().getEmail(), subject, message, false, false);
        }

        // Send to invited users (if you have them mapped in entity)
        if (request.getInvitedUsers() != null) {
            request
                .getInvitedUsers()
                .forEach(user -> {
                    if (user.getEmail() != null) {
                        mailService.sendEmail(user.getEmail(), subject, message, false, false);
                    }
                });
        }
    }

    /**
     * Partially update a bookingRequest.
     *
     * @param bookingRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookingRequestDTO> partialUpdate(BookingRequestDTO bookingRequestDTO) {
        LOG.debug("Request to partially update BookingRequest : {}", bookingRequestDTO);

        return bookingRequestRepository
            .findById(bookingRequestDTO.getId())
            .map(existingBookingRequest -> {
                bookingRequestMapper.partialUpdate(existingBookingRequest, bookingRequestDTO);

                return existingBookingRequest;
            })
            .map(bookingRequestRepository::save)
            .map(bookingRequestMapper::toDto);
    }

    /**
     * Get all the bookingRequests with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BookingRequestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookingRequestRepository.findAllWithEagerRelationships(pageable).map(bookingRequestMapper::toDto);
    }

    /**
     * Get one bookingRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookingRequestDTO> findOne(Long id) {
        LOG.debug("Request to get BookingRequest : {}", id);
        return bookingRequestRepository.findOneWithEagerRelationships(id).map(bookingRequestMapper::toDto);
    }

    /**
     * Delete the bookingRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BookingRequest : {}", id);
        bookingRequestRepository.deleteById(id);
    }
}
