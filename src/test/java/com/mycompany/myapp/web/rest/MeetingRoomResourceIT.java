package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MeetingRoomAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Equipment;
import com.mycompany.myapp.domain.MeetingRoom;
import com.mycompany.myapp.repository.MeetingRoomRepository;
import com.mycompany.myapp.service.MeetingRoomService;
import com.mycompany.myapp.service.dto.MeetingRoomDTO;
import com.mycompany.myapp.service.mapper.MeetingRoomMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link MeetingRoomResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MeetingRoomResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;
    private static final Integer SMALLER_CAPACITY = 1 - 1;

    private static final Boolean DEFAULT_REQUIRES_APPROVAL = false;
    private static final Boolean UPDATED_REQUIRES_APPROVAL = true;

    private static final String ENTITY_API_URL = "/api/meeting-rooms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Mock
    private MeetingRoomRepository meetingRoomRepositoryMock;

    @Autowired
    private MeetingRoomMapper meetingRoomMapper;

    @Mock
    private MeetingRoomService meetingRoomServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMeetingRoomMockMvc;

    private MeetingRoom meetingRoom;

    private MeetingRoom insertedMeetingRoom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MeetingRoom createEntity() {
        return new MeetingRoom().name(DEFAULT_NAME).capacity(DEFAULT_CAPACITY).requiresApproval(DEFAULT_REQUIRES_APPROVAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MeetingRoom createUpdatedEntity() {
        return new MeetingRoom().name(UPDATED_NAME).capacity(UPDATED_CAPACITY).requiresApproval(UPDATED_REQUIRES_APPROVAL);
    }

    @BeforeEach
    void initTest() {
        meetingRoom = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMeetingRoom != null) {
            meetingRoomRepository.delete(insertedMeetingRoom);
            insertedMeetingRoom = null;
        }
    }

    @Test
    @Transactional
    void createMeetingRoom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MeetingRoom
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);
        var returnedMeetingRoomDTO = om.readValue(
            restMeetingRoomMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(meetingRoomDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MeetingRoomDTO.class
        );

        // Validate the MeetingRoom in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMeetingRoom = meetingRoomMapper.toEntity(returnedMeetingRoomDTO);
        assertMeetingRoomUpdatableFieldsEquals(returnedMeetingRoom, getPersistedMeetingRoom(returnedMeetingRoom));

        insertedMeetingRoom = returnedMeetingRoom;
    }

    @Test
    @Transactional
    void createMeetingRoomWithExistingId() throws Exception {
        // Create the MeetingRoom with an existing ID
        meetingRoom.setId(1L);
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMeetingRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(meetingRoomDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MeetingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        meetingRoom.setName(null);

        // Create the MeetingRoom, which fails.
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        restMeetingRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(meetingRoomDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        meetingRoom.setCapacity(null);

        // Create the MeetingRoom, which fails.
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        restMeetingRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(meetingRoomDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRequiresApprovalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        meetingRoom.setRequiresApproval(null);

        // Create the MeetingRoom, which fails.
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        restMeetingRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(meetingRoomDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMeetingRooms() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList
        restMeetingRoomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(meetingRoom.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
            .andExpect(jsonPath("$.[*].requiresApproval").value(hasItem(DEFAULT_REQUIRES_APPROVAL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMeetingRoomsWithEagerRelationshipsIsEnabled() throws Exception {
        when(meetingRoomServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMeetingRoomMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(meetingRoomServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMeetingRoomsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(meetingRoomServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMeetingRoomMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(meetingRoomRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMeetingRoom() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get the meetingRoom
        restMeetingRoomMockMvc
            .perform(get(ENTITY_API_URL_ID, meetingRoom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(meetingRoom.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.capacity").value(DEFAULT_CAPACITY))
            .andExpect(jsonPath("$.requiresApproval").value(DEFAULT_REQUIRES_APPROVAL));
    }

    @Test
    @Transactional
    void getMeetingRoomsByIdFiltering() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        Long id = meetingRoom.getId();

        defaultMeetingRoomFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMeetingRoomFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMeetingRoomFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where name equals to
        defaultMeetingRoomFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where name in
        defaultMeetingRoomFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where name is not null
        defaultMeetingRoomFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where name contains
        defaultMeetingRoomFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where name does not contain
        defaultMeetingRoomFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByCapacityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where capacity equals to
        defaultMeetingRoomFiltering("capacity.equals=" + DEFAULT_CAPACITY, "capacity.equals=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByCapacityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where capacity in
        defaultMeetingRoomFiltering("capacity.in=" + DEFAULT_CAPACITY + "," + UPDATED_CAPACITY, "capacity.in=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByCapacityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where capacity is not null
        defaultMeetingRoomFiltering("capacity.specified=true", "capacity.specified=false");
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByCapacityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where capacity is greater than or equal to
        defaultMeetingRoomFiltering(
            "capacity.greaterThanOrEqual=" + DEFAULT_CAPACITY,
            "capacity.greaterThanOrEqual=" + (DEFAULT_CAPACITY + 1)
        );
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByCapacityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where capacity is less than or equal to
        defaultMeetingRoomFiltering("capacity.lessThanOrEqual=" + DEFAULT_CAPACITY, "capacity.lessThanOrEqual=" + SMALLER_CAPACITY);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByCapacityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where capacity is less than
        defaultMeetingRoomFiltering("capacity.lessThan=" + (DEFAULT_CAPACITY + 1), "capacity.lessThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByCapacityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where capacity is greater than
        defaultMeetingRoomFiltering("capacity.greaterThan=" + SMALLER_CAPACITY, "capacity.greaterThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByRequiresApprovalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where requiresApproval equals to
        defaultMeetingRoomFiltering(
            "requiresApproval.equals=" + DEFAULT_REQUIRES_APPROVAL,
            "requiresApproval.equals=" + UPDATED_REQUIRES_APPROVAL
        );
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByRequiresApprovalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where requiresApproval in
        defaultMeetingRoomFiltering(
            "requiresApproval.in=" + DEFAULT_REQUIRES_APPROVAL + "," + UPDATED_REQUIRES_APPROVAL,
            "requiresApproval.in=" + UPDATED_REQUIRES_APPROVAL
        );
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByRequiresApprovalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        // Get all the meetingRoomList where requiresApproval is not null
        defaultMeetingRoomFiltering("requiresApproval.specified=true", "requiresApproval.specified=false");
    }

    @Test
    @Transactional
    void getAllMeetingRoomsByEquipmentIsEqualToSomething() throws Exception {
        Equipment equipment;
        if (TestUtil.findAll(em, Equipment.class).isEmpty()) {
            meetingRoomRepository.saveAndFlush(meetingRoom);
            equipment = EquipmentResourceIT.createEntity();
        } else {
            equipment = TestUtil.findAll(em, Equipment.class).get(0);
        }
        em.persist(equipment);
        em.flush();
        meetingRoom.addEquipment(equipment);
        meetingRoomRepository.saveAndFlush(meetingRoom);
        Long equipmentId = equipment.getId();
        // Get all the meetingRoomList where equipment equals to equipmentId
        defaultMeetingRoomShouldBeFound("equipmentId.equals=" + equipmentId);

        // Get all the meetingRoomList where equipment equals to (equipmentId + 1)
        defaultMeetingRoomShouldNotBeFound("equipmentId.equals=" + (equipmentId + 1));
    }

    private void defaultMeetingRoomFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultMeetingRoomShouldBeFound(shouldBeFound);
        defaultMeetingRoomShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMeetingRoomShouldBeFound(String filter) throws Exception {
        restMeetingRoomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(meetingRoom.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
            .andExpect(jsonPath("$.[*].requiresApproval").value(hasItem(DEFAULT_REQUIRES_APPROVAL)));

        // Check, that the count call also returns 1
        restMeetingRoomMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMeetingRoomShouldNotBeFound(String filter) throws Exception {
        restMeetingRoomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMeetingRoomMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMeetingRoom() throws Exception {
        // Get the meetingRoom
        restMeetingRoomMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMeetingRoom() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the meetingRoom
        MeetingRoom updatedMeetingRoom = meetingRoomRepository.findById(meetingRoom.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMeetingRoom are not directly saved in db
        em.detach(updatedMeetingRoom);
        updatedMeetingRoom.name(UPDATED_NAME).capacity(UPDATED_CAPACITY).requiresApproval(UPDATED_REQUIRES_APPROVAL);
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(updatedMeetingRoom);

        restMeetingRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, meetingRoomDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(meetingRoomDTO))
            )
            .andExpect(status().isOk());

        // Validate the MeetingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMeetingRoomToMatchAllProperties(updatedMeetingRoom);
    }

    @Test
    @Transactional
    void putNonExistingMeetingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        meetingRoom.setId(longCount.incrementAndGet());

        // Create the MeetingRoom
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMeetingRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, meetingRoomDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(meetingRoomDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MeetingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMeetingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        meetingRoom.setId(longCount.incrementAndGet());

        // Create the MeetingRoom
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeetingRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(meetingRoomDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MeetingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMeetingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        meetingRoom.setId(longCount.incrementAndGet());

        // Create the MeetingRoom
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeetingRoomMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(meetingRoomDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MeetingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMeetingRoomWithPatch() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the meetingRoom using partial update
        MeetingRoom partialUpdatedMeetingRoom = new MeetingRoom();
        partialUpdatedMeetingRoom.setId(meetingRoom.getId());

        partialUpdatedMeetingRoom.capacity(UPDATED_CAPACITY).requiresApproval(UPDATED_REQUIRES_APPROVAL);

        restMeetingRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMeetingRoom.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMeetingRoom))
            )
            .andExpect(status().isOk());

        // Validate the MeetingRoom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMeetingRoomUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMeetingRoom, meetingRoom),
            getPersistedMeetingRoom(meetingRoom)
        );
    }

    @Test
    @Transactional
    void fullUpdateMeetingRoomWithPatch() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the meetingRoom using partial update
        MeetingRoom partialUpdatedMeetingRoom = new MeetingRoom();
        partialUpdatedMeetingRoom.setId(meetingRoom.getId());

        partialUpdatedMeetingRoom.name(UPDATED_NAME).capacity(UPDATED_CAPACITY).requiresApproval(UPDATED_REQUIRES_APPROVAL);

        restMeetingRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMeetingRoom.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMeetingRoom))
            )
            .andExpect(status().isOk());

        // Validate the MeetingRoom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMeetingRoomUpdatableFieldsEquals(partialUpdatedMeetingRoom, getPersistedMeetingRoom(partialUpdatedMeetingRoom));
    }

    @Test
    @Transactional
    void patchNonExistingMeetingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        meetingRoom.setId(longCount.incrementAndGet());

        // Create the MeetingRoom
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMeetingRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, meetingRoomDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(meetingRoomDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MeetingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMeetingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        meetingRoom.setId(longCount.incrementAndGet());

        // Create the MeetingRoom
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeetingRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(meetingRoomDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MeetingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMeetingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        meetingRoom.setId(longCount.incrementAndGet());

        // Create the MeetingRoom
        MeetingRoomDTO meetingRoomDTO = meetingRoomMapper.toDto(meetingRoom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeetingRoomMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(meetingRoomDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MeetingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMeetingRoom() throws Exception {
        // Initialize the database
        insertedMeetingRoom = meetingRoomRepository.saveAndFlush(meetingRoom);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the meetingRoom
        restMeetingRoomMockMvc
            .perform(delete(ENTITY_API_URL_ID, meetingRoom.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return meetingRoomRepository.count();
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

    protected MeetingRoom getPersistedMeetingRoom(MeetingRoom meetingRoom) {
        return meetingRoomRepository.findById(meetingRoom.getId()).orElseThrow();
    }

    protected void assertPersistedMeetingRoomToMatchAllProperties(MeetingRoom expectedMeetingRoom) {
        assertMeetingRoomAllPropertiesEquals(expectedMeetingRoom, getPersistedMeetingRoom(expectedMeetingRoom));
    }

    protected void assertPersistedMeetingRoomToMatchUpdatableProperties(MeetingRoom expectedMeetingRoom) {
        assertMeetingRoomAllUpdatablePropertiesEquals(expectedMeetingRoom, getPersistedMeetingRoom(expectedMeetingRoom));
    }
}
