package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.BookingRequest;
import com.mycompany.myapp.repository.BookingRequestRepository;
import com.mycompany.myapp.service.dto.BookingRequestDTO;
import com.mycompany.myapp.service.mapper.BookingRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.BookingRequest}.
 */
@Service
@Transactional
public class BookingRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingRequestService.class);

    private final BookingRequestRepository bookingRequestRepository;

    private final BookingRequestMapper bookingRequestMapper;

    public BookingRequestService(BookingRequestRepository bookingRequestRepository, BookingRequestMapper bookingRequestMapper) {
        this.bookingRequestRepository = bookingRequestRepository;
        this.bookingRequestMapper = bookingRequestMapper;
    }

    /**
     * Save a bookingRequest.
     *
     * @param bookingRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public BookingRequestDTO save(BookingRequestDTO bookingRequestDTO) {
        LOG.debug("Request to save BookingRequest : {}", bookingRequestDTO);
        BookingRequest bookingRequest = bookingRequestMapper.toEntity(bookingRequestDTO);
        bookingRequest = bookingRequestRepository.save(bookingRequest);
        return bookingRequestMapper.toDto(bookingRequest);
    }

    /**
     * Update a bookingRequest.
     *
     * @param bookingRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public BookingRequestDTO update(BookingRequestDTO bookingRequestDTO) {
        LOG.debug("Request to update BookingRequest : {}", bookingRequestDTO);
        BookingRequest bookingRequest = bookingRequestMapper.toEntity(bookingRequestDTO);
        bookingRequest = bookingRequestRepository.save(bookingRequest);
        return bookingRequestMapper.toDto(bookingRequest);
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
