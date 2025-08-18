package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.VacationRequestRepository;
import com.mycompany.myapp.service.VacationRequestQueryService;
import com.mycompany.myapp.service.VacationRequestServiceExtension;
import com.mycompany.myapp.service.criteria.VacationRequestCriteria;
import com.mycompany.myapp.service.dto.VacationRequestDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
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
 * Extended REST controller for managing VacationRequests using VacationRequestServiceExtension.
 */
@RestController
@RequestMapping("/api/v1/vacation-requests")
public class VacationRequestResourceExtension {

    private static final Logger LOG = LoggerFactory.getLogger(VacationRequestResourceExtension.class);

    private static final String ENTITY_NAME = "vacationRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VacationRequestServiceExtension vacationRequestServiceExtension;
    private final VacationRequestRepository vacationRequestRepository;
    private final VacationRequestQueryService vacationRequestQueryService;

    public VacationRequestResourceExtension(
        VacationRequestServiceExtension vacationRequestServiceExtension,
        VacationRequestRepository vacationRequestRepository,
        VacationRequestQueryService vacationRequestQueryService
    ) {
        this.vacationRequestServiceExtension = vacationRequestServiceExtension;
        this.vacationRequestRepository = vacationRequestRepository;
        this.vacationRequestQueryService = vacationRequestQueryService;
    }

    /**
     * {@code GET /my} : get all vacation requests for the current user.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vacationRequests in body.
     */
    @GetMapping("/my")
    public ResponseEntity<List<VacationRequestDTO>> getMyVacationRequests() {
        LOG.debug("REST request to get VacationRequests for current user");
        List<VacationRequestDTO> requests = vacationRequestServiceExtension.findMyVacationRequests();
        return ResponseEntity.ok().body(requests);
    }

    /**
     * {@code GET /} : get all vacationRequests with optional criteria and pagination.
     */
    @GetMapping("")
    public ResponseEntity<List<VacationRequestDTO>> getAllVacationRequests(
        VacationRequestCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get all VacationRequests by criteria: {}", criteria);

        Page<VacationRequestDTO> page = vacationRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET /count} : count all vacationRequests matching criteria.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countVacationRequests(VacationRequestCriteria criteria) {
        LOG.debug("REST request to count VacationRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(vacationRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET /{id}} : get a vacationRequest by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VacationRequestDTO> getVacationRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get VacationRequest : {}", id);
        Optional<VacationRequestDTO> vacationRequestDTO = vacationRequestServiceExtension.findOne(id);
        return ResponseUtil.wrapOrNotFound(vacationRequestDTO);
    }

    /**
     * {@code DELETE /{id}} : delete a vacationRequest by id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVacationRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete VacationRequest : {}", id);
        vacationRequestServiceExtension.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST /} : create a new vacationRequest.
     *
     * @param vacationRequestDTO the vacationRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vacationRequestDTO, or with status {@code 400 (Bad Request)} if the vacationRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VacationRequestDTO> createVacationRequest(@Valid @RequestBody VacationRequestDTO dto) throws URISyntaxException {
        if (dto.getId() != null) {
            throw new BadRequestAlertException("A new vacationRequest cannot already have an ID", "vacationRequest", "idexists");
        }
        VacationRequestDTO result = vacationRequestServiceExtension.save(dto);
        return ResponseEntity.created(new URI("/api/v1/vacation-requests/" + result.getId())).body(result);
    }

    /**
     * {@code PUT /} : update an existing vacationRequest.
     *
     * @param vacationRequestDTO the vacationRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vacationRequestDTO, or with status {@code 400 (Bad Request)} if the vacationRequestDTO is not valid, or with status {@code 500 (Internal Server Error)} if the vacationRequestDTO couldn't be updated.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VacationRequestDTO> updateVacationRequest(@PathVariable Long id, @Valid @RequestBody VacationRequestDTO dto) {
        if (!Objects.equals(id, dto.getId())) {
            throw new BadRequestAlertException("Invalid ID", "vacationRequest", "idinvalid");
        }

        if (!vacationRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", "vacationRequest", "idnotfound");
        }

        VacationRequestDTO result = vacationRequestServiceExtension.update(dto);
        return ResponseEntity.ok().body(result);
    }
}
