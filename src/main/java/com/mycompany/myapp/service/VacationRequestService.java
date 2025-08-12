package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.VacationRequest;
import com.mycompany.myapp.repository.VacationRequestRepository;
import com.mycompany.myapp.service.dto.VacationRequestDTO;
import com.mycompany.myapp.service.mapper.VacationRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.VacationRequest}.
 */
@Service
@Transactional
public class VacationRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(VacationRequestService.class);

    private final VacationRequestRepository vacationRequestRepository;

    private final VacationRequestMapper vacationRequestMapper;

    public VacationRequestService(VacationRequestRepository vacationRequestRepository, VacationRequestMapper vacationRequestMapper) {
        this.vacationRequestRepository = vacationRequestRepository;
        this.vacationRequestMapper = vacationRequestMapper;
    }

    /**
     * Save a vacationRequest.
     *
     * @param vacationRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public VacationRequestDTO save(VacationRequestDTO vacationRequestDTO) {
        LOG.debug("Request to save VacationRequest : {}", vacationRequestDTO);
        VacationRequest vacationRequest = vacationRequestMapper.toEntity(vacationRequestDTO);
        vacationRequest = vacationRequestRepository.save(vacationRequest);
        return vacationRequestMapper.toDto(vacationRequest);
    }

    /**
     * Update a vacationRequest.
     *
     * @param vacationRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public VacationRequestDTO update(VacationRequestDTO vacationRequestDTO) {
        LOG.debug("Request to update VacationRequest : {}", vacationRequestDTO);
        VacationRequest vacationRequest = vacationRequestMapper.toEntity(vacationRequestDTO);
        vacationRequest = vacationRequestRepository.save(vacationRequest);
        return vacationRequestMapper.toDto(vacationRequest);
    }

    /**
     * Partially update a vacationRequest.
     *
     * @param vacationRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VacationRequestDTO> partialUpdate(VacationRequestDTO vacationRequestDTO) {
        LOG.debug("Request to partially update VacationRequest : {}", vacationRequestDTO);

        return vacationRequestRepository
            .findById(vacationRequestDTO.getId())
            .map(existingVacationRequest -> {
                vacationRequestMapper.partialUpdate(existingVacationRequest, vacationRequestDTO);

                return existingVacationRequest;
            })
            .map(vacationRequestRepository::save)
            .map(vacationRequestMapper::toDto);
    }

    /**
     * Get one vacationRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VacationRequestDTO> findOne(Long id) {
        LOG.debug("Request to get VacationRequest : {}", id);
        return vacationRequestRepository.findById(id).map(vacationRequestMapper::toDto);
    }

    /**
     * Delete the vacationRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete VacationRequest : {}", id);
        vacationRequestRepository.deleteById(id);
    }
}
