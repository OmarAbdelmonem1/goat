package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.VacationRequest;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VacationRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long>, JpaSpecificationExecutor<VacationRequest> {
    /**
     * Find VacationRequests by Employee ID.
     *
     * @param employeeId the ID of the employee
     * @return a list of VacationRequests associated with the given employee ID
     */
    List<VacationRequest> findByEmployeeId(Long employeeId);
}
