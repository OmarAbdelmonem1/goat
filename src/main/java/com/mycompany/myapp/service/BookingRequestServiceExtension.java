package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.MeetingRoomRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.criteria.BookingRequestCriteria;
import com.mycompany.myapp.service.dto.BookingRequestDTO;
import com.mycompany.myapp.service.mapper.MeetingRoomMapper;
import com.mycompany.myapp.web.rest.MeetingRoomResource;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.PaginationUtil;

@Service
@Transactional(readOnly = true)
public class BookingRequestServiceExtension {

    private static final Logger LOG = LoggerFactory.getLogger(BookingRequestServiceExtension.class);
    private static final String ENTITY_NAME = "bookingRequest";

    private final BookingRequestQueryService bookingRequestQueryService;
    private final EmployeeRepository employeeRepository;

    public BookingRequestServiceExtension(BookingRequestQueryService bookingRequestQueryService, EmployeeRepository employeeRepository) {
        this.bookingRequestQueryService = bookingRequestQueryService;
        this.employeeRepository = employeeRepository;
    }

    public Page<BookingRequestDTO> findMyBookingRequests(Pageable pageable) {
        LOG.debug("Service: get booking requests for current user");

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        Employee employee = employeeRepository
            .findOneByUserLogin(currentUserLogin)
            .orElseThrow(() -> new IllegalStateException("Employee not found"));

        BookingRequestCriteria criteria = new BookingRequestCriteria();
        LongFilter employeeIdFilter = new LongFilter();
        employeeIdFilter.setEquals(employee.getId());
        criteria.setEmployeeId(employeeIdFilter);

        return bookingRequestQueryService.findByCriteria(criteria, pageable);
    }
}
