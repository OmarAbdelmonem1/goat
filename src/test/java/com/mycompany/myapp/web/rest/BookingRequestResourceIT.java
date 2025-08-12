package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.BookingRequestAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.BookingRequest;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.MeetingRoom;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.BookingRequestRepository;
import com.mycompany.myapp.service.BookingRequestService;
import com.mycompany.myapp.service.dto.BookingRequestDTO;
import com.mycompany.myapp.service.mapper.BookingRequestMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookingRequestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookingRequestResourceIT {

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Status DEFAULT_STATUS = Status.PENDING;
    private static final Status UPDATED_STATUS = Status.APPROVED;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_PURPOSE = "AAAAAAAAAA";
    private static final String UPDATED_PURPOSE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/booking-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookingRequestRepository bookingRequestRepository;

    @Mock
    private BookingRequestRepository bookingRequestRepositoryMock;

    @Autowired
    private BookingRequestMapper bookingRequestMapper;

    @Mock
    private BookingRequestService bookingRequestServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookingRequestMockMvc;

    private BookingRequest bookingRequest;

    private BookingRequest insertedBookingRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookingRequest createEntity(EntityManager em) {
        BookingRequest bookingRequest = new BookingRequest()
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .purpose(DEFAULT_PURPOSE);
        // Add required entity
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employee = EmployeeResourceIT.createEntity();
            em.persist(employee);
            em.flush();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        bookingRequest.setEmployee(employee);
        // Add required entity
        MeetingRoom meetingRoom;
        if (TestUtil.findAll(em, MeetingRoom.class).isEmpty()) {
            meetingRoom = MeetingRoomResourceIT.createEntity();
            em.persist(meetingRoom);
            em.flush();
        } else {
            meetingRoom = TestUtil.findAll(em, MeetingRoom.class).get(0);
        }
        bookingRequest.setMeetingRoom(meetingRoom);
        return bookingRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookingRequest createUpdatedEntity(EntityManager em) {
        BookingRequest updatedBookingRequest = new BookingRequest()
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .purpose(UPDATED_PURPOSE);
        // Add required entity
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employee = EmployeeResourceIT.createUpdatedEntity();
            em.persist(employee);
            em.flush();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        updatedBookingRequest.setEmployee(employee);
        // Add required entity
        MeetingRoom meetingRoom;
        if (TestUtil.findAll(em, MeetingRoom.class).isEmpty()) {
            meetingRoom = MeetingRoomResourceIT.createUpdatedEntity();
            em.persist(meetingRoom);
            em.flush();
        } else {
            meetingRoom = TestUtil.findAll(em, MeetingRoom.class).get(0);
        }
        updatedBookingRequest.setMeetingRoom(meetingRoom);
        return updatedBookingRequest;
    }

    @BeforeEach
    void initTest() {
        bookingRequest = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedBookingRequest != null) {
            bookingRequestRepository.delete(insertedBookingRequest);
            insertedBookingRequest = null;
        }
    }

    @Test
    @Transactional
    void createBookingRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookingRequest
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);
        var returnedBookingRequestDTO = om.readValue(
            restBookingRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookingRequestDTO.class
        );

        // Validate the BookingRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBookingRequest = bookingRequestMapper.toEntity(returnedBookingRequestDTO);
        assertBookingRequestUpdatableFieldsEquals(returnedBookingRequest, getPersistedBookingRequest(returnedBookingRequest));

        insertedBookingRequest = returnedBookingRequest;
    }

    @Test
    @Transactional
    void createBookingRequestWithExistingId() throws Exception {
        // Create the BookingRequest with an existing ID
        bookingRequest.setId(1L);
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookingRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookingRequest.setStartTime(null);

        // Create the BookingRequest, which fails.
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        restBookingRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookingRequest.setEndTime(null);

        // Create the BookingRequest, which fails.
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        restBookingRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookingRequest.setStatus(null);

        // Create the BookingRequest, which fails.
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        restBookingRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookingRequest.setCreatedAt(null);

        // Create the BookingRequest, which fails.
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        restBookingRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBookingRequests() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList
        restBookingRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookingRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].purpose").value(hasItem(DEFAULT_PURPOSE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookingRequestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookingRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookingRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookingRequestServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookingRequestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookingRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookingRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookingRequestRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBookingRequest() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get the bookingRequest
        restBookingRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, bookingRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookingRequest.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.purpose").value(DEFAULT_PURPOSE));
    }

    @Test
    @Transactional
    void getBookingRequestsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        Long id = bookingRequest.getId();

        defaultBookingRequestFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookingRequestFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookingRequestFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where startTime equals to
        defaultBookingRequestFiltering("startTime.equals=" + DEFAULT_START_TIME, "startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where startTime in
        defaultBookingRequestFiltering(
            "startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME,
            "startTime.in=" + UPDATED_START_TIME
        );
    }

    @Test
    @Transactional
    void getAllBookingRequestsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where startTime is not null
        defaultBookingRequestFiltering("startTime.specified=true", "startTime.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingRequestsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where endTime equals to
        defaultBookingRequestFiltering("endTime.equals=" + DEFAULT_END_TIME, "endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where endTime in
        defaultBookingRequestFiltering("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME, "endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where endTime is not null
        defaultBookingRequestFiltering("endTime.specified=true", "endTime.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where status equals to
        defaultBookingRequestFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where status in
        defaultBookingRequestFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where status is not null
        defaultBookingRequestFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingRequestsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where createdAt equals to
        defaultBookingRequestFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where createdAt in
        defaultBookingRequestFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllBookingRequestsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where createdAt is not null
        defaultBookingRequestFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingRequestsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where updatedAt equals to
        defaultBookingRequestFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where updatedAt in
        defaultBookingRequestFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllBookingRequestsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where updatedAt is not null
        defaultBookingRequestFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingRequestsByPurposeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where purpose equals to
        defaultBookingRequestFiltering("purpose.equals=" + DEFAULT_PURPOSE, "purpose.equals=" + UPDATED_PURPOSE);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByPurposeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where purpose in
        defaultBookingRequestFiltering("purpose.in=" + DEFAULT_PURPOSE + "," + UPDATED_PURPOSE, "purpose.in=" + UPDATED_PURPOSE);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByPurposeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where purpose is not null
        defaultBookingRequestFiltering("purpose.specified=true", "purpose.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingRequestsByPurposeContainsSomething() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where purpose contains
        defaultBookingRequestFiltering("purpose.contains=" + DEFAULT_PURPOSE, "purpose.contains=" + UPDATED_PURPOSE);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByPurposeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        // Get all the bookingRequestList where purpose does not contain
        defaultBookingRequestFiltering("purpose.doesNotContain=" + UPDATED_PURPOSE, "purpose.doesNotContain=" + DEFAULT_PURPOSE);
    }

    @Test
    @Transactional
    void getAllBookingRequestsByInvitedUsersIsEqualToSomething() throws Exception {
        Employee invitedUsers;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            bookingRequestRepository.saveAndFlush(bookingRequest);
            invitedUsers = EmployeeResourceIT.createEntity();
        } else {
            invitedUsers = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(invitedUsers);
        em.flush();
        bookingRequest.addInvitedUsers(invitedUsers);
        bookingRequestRepository.saveAndFlush(bookingRequest);
        Long invitedUsersId = invitedUsers.getId();
        // Get all the bookingRequestList where invitedUsers equals to invitedUsersId
        defaultBookingRequestShouldBeFound("invitedUsersId.equals=" + invitedUsersId);

        // Get all the bookingRequestList where invitedUsers equals to (invitedUsersId + 1)
        defaultBookingRequestShouldNotBeFound("invitedUsersId.equals=" + (invitedUsersId + 1));
    }

    @Test
    @Transactional
    void getAllBookingRequestsByEmployeeIsEqualToSomething() throws Exception {
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            bookingRequestRepository.saveAndFlush(bookingRequest);
            employee = EmployeeResourceIT.createEntity();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(employee);
        em.flush();
        bookingRequest.setEmployee(employee);
        bookingRequestRepository.saveAndFlush(bookingRequest);
        Long employeeId = employee.getId();
        // Get all the bookingRequestList where employee equals to employeeId
        defaultBookingRequestShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the bookingRequestList where employee equals to (employeeId + 1)
        defaultBookingRequestShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    @Test
    @Transactional
    void getAllBookingRequestsByMeetingRoomIsEqualToSomething() throws Exception {
        MeetingRoom meetingRoom;
        if (TestUtil.findAll(em, MeetingRoom.class).isEmpty()) {
            bookingRequestRepository.saveAndFlush(bookingRequest);
            meetingRoom = MeetingRoomResourceIT.createEntity();
        } else {
            meetingRoom = TestUtil.findAll(em, MeetingRoom.class).get(0);
        }
        em.persist(meetingRoom);
        em.flush();
        bookingRequest.setMeetingRoom(meetingRoom);
        bookingRequestRepository.saveAndFlush(bookingRequest);
        Long meetingRoomId = meetingRoom.getId();
        // Get all the bookingRequestList where meetingRoom equals to meetingRoomId
        defaultBookingRequestShouldBeFound("meetingRoomId.equals=" + meetingRoomId);

        // Get all the bookingRequestList where meetingRoom equals to (meetingRoomId + 1)
        defaultBookingRequestShouldNotBeFound("meetingRoomId.equals=" + (meetingRoomId + 1));
    }

    private void defaultBookingRequestFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookingRequestShouldBeFound(shouldBeFound);
        defaultBookingRequestShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookingRequestShouldBeFound(String filter) throws Exception {
        restBookingRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookingRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].purpose").value(hasItem(DEFAULT_PURPOSE)));

        // Check, that the count call also returns 1
        restBookingRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookingRequestShouldNotBeFound(String filter) throws Exception {
        restBookingRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookingRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBookingRequest() throws Exception {
        // Get the bookingRequest
        restBookingRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookingRequest() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookingRequest
        BookingRequest updatedBookingRequest = bookingRequestRepository.findById(bookingRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookingRequest are not directly saved in db
        em.detach(updatedBookingRequest);
        updatedBookingRequest
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .purpose(UPDATED_PURPOSE);
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(updatedBookingRequest);

        restBookingRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookingRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookingRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the BookingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookingRequestToMatchAllProperties(updatedBookingRequest);
    }

    @Test
    @Transactional
    void putNonExistingBookingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingRequest.setId(longCount.incrementAndGet());

        // Create the BookingRequest
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookingRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookingRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingRequest.setId(longCount.incrementAndGet());

        // Create the BookingRequest
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookingRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingRequest.setId(longCount.incrementAndGet());

        // Create the BookingRequest
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookingRequestWithPatch() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookingRequest using partial update
        BookingRequest partialUpdatedBookingRequest = new BookingRequest();
        partialUpdatedBookingRequest.setId(bookingRequest.getId());

        partialUpdatedBookingRequest.endTime(UPDATED_END_TIME).status(UPDATED_STATUS);

        restBookingRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookingRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookingRequest))
            )
            .andExpect(status().isOk());

        // Validate the BookingRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookingRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookingRequest, bookingRequest),
            getPersistedBookingRequest(bookingRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookingRequestWithPatch() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookingRequest using partial update
        BookingRequest partialUpdatedBookingRequest = new BookingRequest();
        partialUpdatedBookingRequest.setId(bookingRequest.getId());

        partialUpdatedBookingRequest
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .purpose(UPDATED_PURPOSE);

        restBookingRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookingRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookingRequest))
            )
            .andExpect(status().isOk());

        // Validate the BookingRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookingRequestUpdatableFieldsEquals(partialUpdatedBookingRequest, getPersistedBookingRequest(partialUpdatedBookingRequest));
    }

    @Test
    @Transactional
    void patchNonExistingBookingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingRequest.setId(longCount.incrementAndGet());

        // Create the BookingRequest
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookingRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookingRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingRequest.setId(longCount.incrementAndGet());

        // Create the BookingRequest
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookingRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingRequest.setId(longCount.incrementAndGet());

        // Create the BookingRequest
        BookingRequestDTO bookingRequestDTO = bookingRequestMapper.toDto(bookingRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookingRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookingRequest() throws Exception {
        // Initialize the database
        insertedBookingRequest = bookingRequestRepository.saveAndFlush(bookingRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookingRequest
        restBookingRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookingRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookingRequestRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected BookingRequest getPersistedBookingRequest(BookingRequest bookingRequest) {
        return bookingRequestRepository.findById(bookingRequest.getId()).orElseThrow();
    }

    protected void assertPersistedBookingRequestToMatchAllProperties(BookingRequest expectedBookingRequest) {
        assertBookingRequestAllPropertiesEquals(expectedBookingRequest, getPersistedBookingRequest(expectedBookingRequest));
    }

    protected void assertPersistedBookingRequestToMatchUpdatableProperties(BookingRequest expectedBookingRequest) {
        assertBookingRequestAllUpdatablePropertiesEquals(expectedBookingRequest, getPersistedBookingRequest(expectedBookingRequest));
    }
}
