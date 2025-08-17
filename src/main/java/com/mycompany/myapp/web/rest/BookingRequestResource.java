package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.BookingRequestRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.BookingRequestQueryService;
import com.mycompany.myapp.service.BookingRequestService;
import com.mycompany.myapp.service.criteria.BookingRequestCriteria;
import com.mycompany.myapp.service.dto.BookingRequestDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.BookingRequest}.
 */
@RestController
@RequestMapping("/api/booking-requests")
public class BookingRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookingRequestResource.class);

    private static final String ENTITY_NAME = "bookingRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookingRequestService bookingRequestService;

    private final BookingRequestRepository bookingRequestRepository;

    private final BookingRequestQueryService bookingRequestQueryService;
    private final EmployeeRepository employeeRepository;

    public BookingRequestResource(
        BookingRequestService bookingRequestService,
        BookingRequestRepository bookingRequestRepository,
        BookingRequestQueryService bookingRequestQueryService,
        EmployeeRepository employeeRepository
    ) {
        this.bookingRequestService = bookingRequestService;
        this.bookingRequestRepository = bookingRequestRepository;
        this.bookingRequestQueryService = bookingRequestQueryService;
        this.employeeRepository = employeeRepository;
    }

    /**
     * {@code POST  /booking-requests} : Create a new bookingRequest.
     *
     * @param bookingRequestDTO the bookingRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookingRequestDTO, or with status {@code 400 (Bad Request)} if the bookingRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookingRequestDTO> createBookingRequest(@Valid @RequestBody BookingRequestDTO bookingRequestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save BookingRequest : {}", bookingRequestDTO);

        if (bookingRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new bookingRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }

        BookingRequestDTO result = bookingRequestService.save(bookingRequestDTO);

        return ResponseEntity.created(new URI("/api/booking-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /booking-requests/:id} : Updates an existing bookingRequest.
     *
     * @param id the id of the bookingRequestDTO to save.
     * @param bookingRequestDTO the bookingRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingRequestDTO,
     * or with status {@code 400 (Bad Request)} if the bookingRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookingRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookingRequestDTO> updateBookingRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookingRequestDTO bookingRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BookingRequest : {}, {}", id, bookingRequestDTO);
        if (bookingRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookingRequestDTO = bookingRequestService.update(bookingRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookingRequestDTO.getId().toString()))
            .body(bookingRequestDTO);
    }

    /**
     * {@code PATCH  /booking-requests/:id} : Partial updates given fields of an existing bookingRequest, field will ignore if it is null
     *
     * @param id the id of the bookingRequestDTO to save.
     * @param bookingRequestDTO the bookingRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingRequestDTO,
     * or with status {@code 400 (Bad Request)} if the bookingRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookingRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookingRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookingRequestDTO> partialUpdateBookingRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookingRequestDTO bookingRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BookingRequest partially : {}, {}", id, bookingRequestDTO);
        if (bookingRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookingRequestDTO> result = bookingRequestService.partialUpdate(bookingRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookingRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /booking-requests} : get all the bookingRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookingRequests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookingRequestDTO>> getAllBookingRequests(
        BookingRequestCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BookingRequests by criteria: {}", criteria);

        Page<BookingRequestDTO> page = bookingRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /booking-requests/count} : count all the bookingRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBookingRequests(BookingRequestCriteria criteria) {
        LOG.debug("REST request to count BookingRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookingRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /booking-requests/:id} : get the "id" bookingRequest.
     *
     * @param id the id of the bookingRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookingRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingRequestDTO> getBookingRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BookingRequest : {}", id);
        Optional<BookingRequestDTO> bookingRequestDTO = bookingRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookingRequestDTO);
    }

    /**
     * {@code DELETE  /booking-requests/:id} : delete the "id" bookingRequest.
     *
     * @param id the id of the bookingRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookingRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BookingRequest : {}", id);
        bookingRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
