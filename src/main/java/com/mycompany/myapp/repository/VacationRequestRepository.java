package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.VacationRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VacationRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long>, JpaSpecificationExecutor<VacationRequest> {}
