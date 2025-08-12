package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BookingRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BookingRequest entity.
 *
 * When extending this class, extend BookingRequestRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface BookingRequestRepository
    extends BookingRequestRepositoryWithBagRelationships, JpaRepository<BookingRequest, Long>, JpaSpecificationExecutor<BookingRequest> {
    default Optional<BookingRequest> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<BookingRequest> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<BookingRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
