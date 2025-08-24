package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.MeetingRoomRepository;
import com.mycompany.myapp.service.MeetingRoomQueryService;
import com.mycompany.myapp.service.MeetingRoomService;
import com.mycompany.myapp.service.criteria.MeetingRoomCriteria;
import com.mycompany.myapp.service.dto.MeetingRoomDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.MeetingRoom}.
 */
@RestController
@RequestMapping("/api/meeting-rooms")
@PreAuthorize("hasAnyRole('ADMIN')")
public class MeetingRoomResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingRoomResource.class);

    private static final String ENTITY_NAME = "meetingRoom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetingRoomService meetingRoomService;

    private final MeetingRoomRepository meetingRoomRepository;

    private final MeetingRoomQueryService meetingRoomQueryService;

    public MeetingRoomResource(
        MeetingRoomService meetingRoomService,
        MeetingRoomRepository meetingRoomRepository,
        MeetingRoomQueryService meetingRoomQueryService
    ) {
        this.meetingRoomService = meetingRoomService;
        this.meetingRoomRepository = meetingRoomRepository;
        this.meetingRoomQueryService = meetingRoomQueryService;
    }

    /**
     * {@code POST  /meeting-rooms} : Create a new meetingRoom.
     *
     * @param meetingRoomDTO the meetingRoomDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new meetingRoomDTO, or with status {@code 400 (Bad Request)} if the meetingRoom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MeetingRoomDTO> createMeetingRoom(@Valid @RequestBody MeetingRoomDTO meetingRoomDTO) throws URISyntaxException {
        LOG.debug("REST request to save MeetingRoom : {}", meetingRoomDTO);
        if (meetingRoomDTO.getId() != null) {
            throw new BadRequestAlertException("A new meetingRoom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        meetingRoomDTO = meetingRoomService.save(meetingRoomDTO);
        return ResponseEntity.created(new URI("/api/meeting-rooms/" + meetingRoomDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, meetingRoomDTO.getId().toString()))
            .body(meetingRoomDTO);
    }

    /**
     * {@code PUT  /meeting-rooms/:id} : Updates an existing meetingRoom.
     *
     * @param id the id of the meetingRoomDTO to save.
     * @param meetingRoomDTO the meetingRoomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingRoomDTO,
     * or with status {@code 400 (Bad Request)} if the meetingRoomDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the meetingRoomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingRoomDTO> updateMeetingRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeetingRoomDTO meetingRoomDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MeetingRoom : {}, {}", id, meetingRoomDTO);
        if (meetingRoomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingRoomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingRoomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        meetingRoomDTO = meetingRoomService.update(meetingRoomDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, meetingRoomDTO.getId().toString()))
            .body(meetingRoomDTO);
    }

    /**
     * {@code PATCH  /meeting-rooms/:id} : Partial updates given fields of an existing meetingRoom, field will ignore if it is null
     *
     * @param id the id of the meetingRoomDTO to save.
     * @param meetingRoomDTO the meetingRoomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meetingRoomDTO,
     * or with status {@code 400 (Bad Request)} if the meetingRoomDTO is not valid,
     * or with status {@code 404 (Not Found)} if the meetingRoomDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the meetingRoomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeetingRoomDTO> partialUpdateMeetingRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeetingRoomDTO meetingRoomDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MeetingRoom partially : {}, {}", id, meetingRoomDTO);
        if (meetingRoomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meetingRoomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetingRoomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeetingRoomDTO> result = meetingRoomService.partialUpdate(meetingRoomDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, meetingRoomDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /meeting-rooms} : get all the meetingRooms.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meetingRooms in body.
     */
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("")
    public ResponseEntity<List<MeetingRoomDTO>> getAllMeetingRooms(
        MeetingRoomCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get MeetingRooms by criteria: {}", criteria);

        Page<MeetingRoomDTO> page = meetingRoomQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /meeting-rooms/count} : count all the meetingRooms.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countMeetingRooms(MeetingRoomCriteria criteria) {
        LOG.debug("REST request to count MeetingRooms by criteria: {}", criteria);
        return ResponseEntity.ok().body(meetingRoomQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /meeting-rooms/:id} : get the "id" meetingRoom.
     *
     * @param id the id of the meetingRoomDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the meetingRoomDTO, or with status {@code 404 (Not Found)}.
     */
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<MeetingRoomDTO> getMeetingRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MeetingRoom : {}", id);
        Optional<MeetingRoomDTO> meetingRoomDTO = meetingRoomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(meetingRoomDTO);
    }

    /**
     * {@code DELETE  /meeting-rooms/:id} : delete the "id" meetingRoom.
     *
     * @param id the id of the meetingRoomDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MeetingRoom : {}", id);
        meetingRoomService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
