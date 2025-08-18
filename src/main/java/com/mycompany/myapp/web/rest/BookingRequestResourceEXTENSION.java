package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.BookingRequestRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.BookingRequestQueryService;
import com.mycompany.myapp.service.BookingRequestService;
import com.mycompany.myapp.service.BookingRequestServiceExtension;
import com.mycompany.myapp.service.criteria.BookingRequestCriteria;
import com.mycompany.myapp.service.dto.BookingRequestDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/v1/booking-requests")
public class BookingRequestResourceEXTENSION extends BookingRequestResource {

    private final BookingRequestServiceExtension bookingRequestServiceExtension;

    public BookingRequestResourceEXTENSION(
        BookingRequestService bookingRequestService,
        BookingRequestRepository bookingRequestRepository,
        BookingRequestQueryService bookingRequestQueryService,
        EmployeeRepository employeeRepository,
        BookingRequestServiceExtension bookingRequestServiceExtension
    ) {
        super(bookingRequestService, bookingRequestRepository, bookingRequestQueryService, employeeRepository);
        this.bookingRequestServiceExtension = bookingRequestServiceExtension;
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingRequestDTO>> getMyBookingRequests(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<BookingRequestDTO> page = bookingRequestServiceExtension.findMyBookingRequests(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/my-invitations")
    public ResponseEntity<List<BookingRequestDTO>> getMyInvitations() {
        List<BookingRequestDTO> invitations = bookingRequestServiceExtension.getInvitationsForCurrentUser();
        return ResponseEntity.ok(invitations);
    }
}
