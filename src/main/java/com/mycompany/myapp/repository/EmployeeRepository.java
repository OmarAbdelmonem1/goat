package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    /**
     * Find an employee by their user login.
     *
     * @param userLogin the login of the user.
     * @return the employee associated with the given user l

    Optional<Long> findById(Long userId);ogin.
     */
    Optional<Employee> findOneByUserLogin(String userLogin);
}
