package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.VacationRequestAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.VacationRequest;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.domain.enumeration.VacationType;
import com.mycompany.myapp.repository.VacationRequestRepository;
import com.mycompany.myapp.service.dto.VacationRequestDTO;
import com.mycompany.myapp.service.mapper.VacationRequestMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VacationRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VacationRequestResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final VacationType DEFAULT_TYPE = VacationType.ANNUAL;
    private static final VacationType UPDATED_TYPE = VacationType.SICK;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.PENDING;
    private static final Status UPDATED_STATUS = Status.APPROVED;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/vacation-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VacationRequestRepository vacationRequestRepository;

    @Autowired
    private VacationRequestMapper vacationRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVacationRequestMockMvc;

    private VacationRequest vacationRequest;

    private VacationRequest insertedVacationRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VacationRequest createEntity(EntityManager em) {
        VacationRequest vacationRequest = new VacationRequest()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .type(DEFAULT_TYPE)
            .reason(DEFAULT_REASON)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employee = EmployeeResourceIT.createEntity();
            em.persist(employee);
            em.flush();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        vacationRequest.setEmployee(employee);
        return vacationRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VacationRequest createUpdatedEntity(EntityManager em) {
        VacationRequest updatedVacationRequest = new VacationRequest()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .type(UPDATED_TYPE)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employee = EmployeeResourceIT.createUpdatedEntity();
            em.persist(employee);
            em.flush();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        updatedVacationRequest.setEmployee(employee);
        return updatedVacationRequest;
    }

    @BeforeEach
    void initTest() {
        vacationRequest = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedVacationRequest != null) {
            vacationRequestRepository.delete(insertedVacationRequest);
            insertedVacationRequest = null;
        }
    }

    @Test
    @Transactional
    void createVacationRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VacationRequest
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);
        var returnedVacationRequestDTO = om.readValue(
            restVacationRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vacationRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VacationRequestDTO.class
        );

        // Validate the VacationRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVacationRequest = vacationRequestMapper.toEntity(returnedVacationRequestDTO);
        assertVacationRequestUpdatableFieldsEquals(returnedVacationRequest, getPersistedVacationRequest(returnedVacationRequest));

        insertedVacationRequest = returnedVacationRequest;
    }

    @Test
    @Transactional
    void createVacationRequestWithExistingId() throws Exception {
        // Create the VacationRequest with an existing ID
        vacationRequest.setId(1L);
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVacationRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vacationRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VacationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vacationRequest.setStartDate(null);

        // Create the VacationRequest, which fails.
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        restVacationRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vacationRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vacationRequest.setEndDate(null);

        // Create the VacationRequest, which fails.
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        restVacationRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vacationRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vacationRequest.setType(null);

        // Create the VacationRequest, which fails.
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        restVacationRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vacationRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vacationRequest.setStatus(null);

        // Create the VacationRequest, which fails.
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        restVacationRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vacationRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVacationRequests() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList
        restVacationRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vacationRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getVacationRequest() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get the vacationRequest
        restVacationRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, vacationRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vacationRequest.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getVacationRequestsByIdFiltering() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        Long id = vacationRequest.getId();

        defaultVacationRequestFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultVacationRequestFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultVacationRequestFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where startDate equals to
        defaultVacationRequestFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where startDate in
        defaultVacationRequestFiltering(
            "startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE,
            "startDate.in=" + UPDATED_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where startDate is not null
        defaultVacationRequestFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where startDate is greater than or equal to
        defaultVacationRequestFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where startDate is less than or equal to
        defaultVacationRequestFiltering(
            "startDate.lessThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.lessThanOrEqual=" + SMALLER_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where startDate is less than
        defaultVacationRequestFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where startDate is greater than
        defaultVacationRequestFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where endDate equals to
        defaultVacationRequestFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where endDate in
        defaultVacationRequestFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where endDate is not null
        defaultVacationRequestFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllVacationRequestsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where endDate is greater than or equal to
        defaultVacationRequestFiltering("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE, "endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where endDate is less than or equal to
        defaultVacationRequestFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where endDate is less than
        defaultVacationRequestFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where endDate is greater than
        defaultVacationRequestFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where type equals to
        defaultVacationRequestFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where type in
        defaultVacationRequestFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where type is not null
        defaultVacationRequestFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where status equals to
        defaultVacationRequestFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where status in
        defaultVacationRequestFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where status is not null
        defaultVacationRequestFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllVacationRequestsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where createdAt equals to
        defaultVacationRequestFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where createdAt in
        defaultVacationRequestFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVacationRequestsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where createdAt is not null
        defaultVacationRequestFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVacationRequestsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where updatedAt equals to
        defaultVacationRequestFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllVacationRequestsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where updatedAt in
        defaultVacationRequestFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVacationRequestsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        // Get all the vacationRequestList where updatedAt is not null
        defaultVacationRequestFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVacationRequestsByEmployeeIsEqualToSomething() throws Exception {
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            vacationRequestRepository.saveAndFlush(vacationRequest);
            employee = EmployeeResourceIT.createEntity();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(employee);
        em.flush();
        vacationRequest.setEmployee(employee);
        vacationRequestRepository.saveAndFlush(vacationRequest);
        Long employeeId = employee.getId();
        // Get all the vacationRequestList where employee equals to employeeId
        defaultVacationRequestShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the vacationRequestList where employee equals to (employeeId + 1)
        defaultVacationRequestShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    private void defaultVacationRequestFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultVacationRequestShouldBeFound(shouldBeFound);
        defaultVacationRequestShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVacationRequestShouldBeFound(String filter) throws Exception {
        restVacationRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vacationRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restVacationRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVacationRequestShouldNotBeFound(String filter) throws Exception {
        restVacationRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVacationRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVacationRequest() throws Exception {
        // Get the vacationRequest
        restVacationRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVacationRequest() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vacationRequest
        VacationRequest updatedVacationRequest = vacationRequestRepository.findById(vacationRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVacationRequest are not directly saved in db
        em.detach(updatedVacationRequest);
        updatedVacationRequest
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .type(UPDATED_TYPE)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(updatedVacationRequest);

        restVacationRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vacationRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vacationRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the VacationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVacationRequestToMatchAllProperties(updatedVacationRequest);
    }

    @Test
    @Transactional
    void putNonExistingVacationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vacationRequest.setId(longCount.incrementAndGet());

        // Create the VacationRequest
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVacationRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vacationRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vacationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VacationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVacationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vacationRequest.setId(longCount.incrementAndGet());

        // Create the VacationRequest
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVacationRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vacationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VacationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVacationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vacationRequest.setId(longCount.incrementAndGet());

        // Create the VacationRequest
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVacationRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vacationRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VacationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVacationRequestWithPatch() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vacationRequest using partial update
        VacationRequest partialUpdatedVacationRequest = new VacationRequest();
        partialUpdatedVacationRequest.setId(vacationRequest.getId());

        partialUpdatedVacationRequest.endDate(UPDATED_END_DATE).type(UPDATED_TYPE).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restVacationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVacationRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVacationRequest))
            )
            .andExpect(status().isOk());

        // Validate the VacationRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVacationRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVacationRequest, vacationRequest),
            getPersistedVacationRequest(vacationRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateVacationRequestWithPatch() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vacationRequest using partial update
        VacationRequest partialUpdatedVacationRequest = new VacationRequest();
        partialUpdatedVacationRequest.setId(vacationRequest.getId());

        partialUpdatedVacationRequest
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .type(UPDATED_TYPE)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restVacationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVacationRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVacationRequest))
            )
            .andExpect(status().isOk());

        // Validate the VacationRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVacationRequestUpdatableFieldsEquals(
            partialUpdatedVacationRequest,
            getPersistedVacationRequest(partialUpdatedVacationRequest)
        );
    }

    @Test
    @Transactional
    void patchNonExistingVacationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vacationRequest.setId(longCount.incrementAndGet());

        // Create the VacationRequest
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVacationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, vacationRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vacationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VacationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVacationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vacationRequest.setId(longCount.incrementAndGet());

        // Create the VacationRequest
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVacationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vacationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VacationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVacationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vacationRequest.setId(longCount.incrementAndGet());

        // Create the VacationRequest
        VacationRequestDTO vacationRequestDTO = vacationRequestMapper.toDto(vacationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVacationRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(vacationRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VacationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVacationRequest() throws Exception {
        // Initialize the database
        insertedVacationRequest = vacationRequestRepository.saveAndFlush(vacationRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the vacationRequest
        restVacationRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, vacationRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return vacationRequestRepository.count();
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

    protected VacationRequest getPersistedVacationRequest(VacationRequest vacationRequest) {
        return vacationRequestRepository.findById(vacationRequest.getId()).orElseThrow();
    }

    protected void assertPersistedVacationRequestToMatchAllProperties(VacationRequest expectedVacationRequest) {
        assertVacationRequestAllPropertiesEquals(expectedVacationRequest, getPersistedVacationRequest(expectedVacationRequest));
    }

    protected void assertPersistedVacationRequestToMatchUpdatableProperties(VacationRequest expectedVacationRequest) {
        assertVacationRequestAllUpdatablePropertiesEquals(expectedVacationRequest, getPersistedVacationRequest(expectedVacationRequest));
    }
}
