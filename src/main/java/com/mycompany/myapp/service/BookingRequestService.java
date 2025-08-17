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
    private final JavaMailSender mailSender;

    public BookingRequestService(
        BookingRequestRepository bookingRequestRepository,
        BookingRequestMapper bookingRequestMapper,
        EmployeeRepository employeeRepository,
        EmployeeMapper employeeMapper,
        MeetingRoomRepository meetingRoomRepository,
        JavaMailSender mailSender
    ) {
        this.bookingRequestRepository = bookingRequestRepository;
        this.bookingRequestMapper = bookingRequestMapper;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.meetingRoomRepository = meetingRoomRepository;
        this.mailSender = mailSender;
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
            throw new BadRequestAlertException("Time slot overlaps with another booking", "bookingRequest", "timeoverlap");
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

        // Send confirmation email for auto-approved bookings or pending requests
        try {
            if (entity.getStatus() == Status.APPROVED) {
                sendBookingConfirmationEmail(entity);
            } else if (entity.getStatus() == Status.PENDING) {
                sendPendingBookingEmail(entity);
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

        BookingRequest existing = bookingRequestRepository
            .findById(bookingRequestDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Booking not found", "bookingRequest", "idnotfound"));

        Status oldStatus = existing.getStatus();

        BookingRequest bookingRequest = bookingRequestMapper.toEntity(bookingRequestDTO);
        bookingRequest = bookingRequestRepository.save(bookingRequest);

        // If status changed, send confirmation
        if (!oldStatus.equals(bookingRequest.getStatus())) {
            try {
                sendStatusUpdateEmail(bookingRequest, oldStatus);
            } catch (Exception e) {
                LOG.error("Failed to send status update email for booking {}: {}", bookingRequest.getId(), e.getMessage());
                // Don't throw exception as update was successful, just log the email error
            }
        }

        return bookingRequestMapper.toDto(bookingRequest);
    }

    /**
     * Send confirmation email for approved bookings
     */
    private void sendBookingConfirmationEmail(BookingRequest booking) {
        if (booking.getEmployee() == null || booking.getEmployee().getEmail() == null) {
            LOG.warn("Cannot send confirmation email: employee or email is null for booking {}", booking.getId());
            return;
        }

        String subject = "Booking Confirmed - " + booking.getMeetingRoom().getName();
        String body = buildConfirmationEmailBody(booking);

        sendEmail(booking.getEmployee().getEmail(), subject, body);
        LOG.info("Sent booking confirmation email to {} for booking {}", booking.getEmployee().getEmail(), booking.getId());
    }

    /**
     * Send notification email for pending bookings
     */
    private void sendPendingBookingEmail(BookingRequest booking) {
        if (booking.getEmployee() == null || booking.getEmployee().getEmail() == null) {
            LOG.warn("Cannot send pending booking email: employee or email is null for booking {}", booking.getId());
            return;
        }

        String subject = "Booking Request Submitted - " + booking.getMeetingRoom().getName();
        String body = buildPendingBookingEmailBody(booking);

        sendEmail(booking.getEmployee().getEmail(), subject, body);
        LOG.info("Sent pending booking notification email to {} for booking {}", booking.getEmployee().getEmail(), booking.getId());
    }

    /**
     * Send status update email when booking status changes
     */
    private void sendStatusUpdateEmail(BookingRequest booking, Status oldStatus) {
        if (booking.getEmployee() == null || booking.getEmployee().getEmail() == null) {
            LOG.warn("Cannot send status update email: employee or email is null for booking {}", booking.getId());
            return;
        }

        String subject = "Booking Status Update - " + booking.getMeetingRoom().getName();
        String body = buildStatusUpdateEmailBody(booking, oldStatus);

        sendEmail(booking.getEmployee().getEmail(), subject, body);
        LOG.info(
            "Sent status update email to {} for booking {} (changed from {} to {})",
            booking.getEmployee().getEmail(),
            booking.getId(),
            oldStatus,
            booking.getStatus()
        );
    }

    /**
     * Build email body for confirmed bookings
     */
    private String buildConfirmationEmailBody(BookingRequest booking) {
        return String.format(
            "Dear %s,\n\n" +
            "Your booking request has been CONFIRMED!\n\n" +
            "Booking Details:\n" +
            "- Meeting Room: %s\n" +
            "- Date & Time: %s to %s\n" +
            "- Purpose: %s\n" +
            "- Booking ID: %s\n\n" +
            "Your meeting room is now reserved for the specified time.\n\n" +
            "Best regards,\n%s",
            booking.getEmployee().getName() != null ? booking.getEmployee().getName() : "User",
            booking.getMeetingRoom().getName(),
            formatDateTime(booking.getStartTime()),
            formatDateTime(booking.getEndTime()),
            booking.getPurpose() != null ? booking.getPurpose() : "Not specified",
            booking.getId(),
            applicationName
        );
    }

    /**
     * Build email body for pending bookings
     */
    private String buildPendingBookingEmailBody(BookingRequest booking) {
        return String.format(
            "Dear %s,\n\n" +
            "Your booking request has been submitted and is PENDING APPROVAL.\n\n" +
            "Booking Details:\n" +
            "- Meeting Room: %s\n" +
            "- Date & Time: %s to %s\n" +
            "- Purpose: %s\n" +
            "- Booking ID: %s\n\n" +
            "You will receive another email once your booking is reviewed and approved/rejected.\n\n" +
            "Best regards,\n%s",
            booking.getEmployee().getName() != null ? booking.getEmployee().getName() : "User",
            booking.getMeetingRoom().getName(),
            formatDateTime(booking.getStartTime()),
            formatDateTime(booking.getEndTime()),
            booking.getPurpose() != null ? booking.getPurpose() : "Not specified",
            booking.getId(),
            applicationName
        );
    }

    /**
     * Build email body for status updates
     */
    private String buildStatusUpdateEmailBody(BookingRequest booking, Status oldStatus) {
        String statusMessage;
        String additionalInfo;

        switch (booking.getStatus()) {
            case APPROVED:
                statusMessage = "APPROVED";
                additionalInfo = "Your meeting room is now confirmed and reserved.";
                break;
            case REJECTED:
                statusMessage = "REJECTED";
                additionalInfo = "Please contact the administrator if you have questions about this decision.";
                break;
            default:
                statusMessage = booking.getStatus().name();
                additionalInfo = "Please check your booking dashboard for more details.";
        }

        return String.format(
            "Dear %s,\n\n" +
            "Your booking status has been updated to: %s\n\n" +
            "Booking Details:\n" +
            "- Meeting Room: %s\n" +
            "- Date & Time: %s to %s\n" +
            "- Purpose: %s\n" +
            "- Booking ID: %s\n" +
            "- Previous Status: %s\n" +
            "- Current Status: %s\n\n" +
            "%s\n\n" +
            "Best regards,\n%s",
            booking.getEmployee().getName() != null ? booking.getEmployee().getName() : "User",
            statusMessage,
            booking.getMeetingRoom().getName(),
            formatDateTime(booking.getStartTime()),
            formatDateTime(booking.getEndTime()),
            booking.getPurpose() != null ? booking.getPurpose() : "Not specified",
            booking.getId(),
            oldStatus.name(),
            booking.getStatus().name(),
            additionalInfo,
            applicationName
        );
    }

    /**
     * Send email using JavaMailSender
     */
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            LOG.debug("Email sent successfully to: {}", to);
        } catch (Exception e) {
            LOG.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Format Instant to readable date-time string
     */
    private String formatDateTime(Instant instant) {
        if (instant == null) {
            return "Not specified";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());
        return formatter.format(instant);
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
