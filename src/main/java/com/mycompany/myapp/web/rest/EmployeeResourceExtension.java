package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.EmployeeServiceExtension;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

@RestController
@RequestMapping("/api/v1/employees")
@PreAuthorize("hasAnyRole('ADMIN')")
public class EmployeeResourceExtension {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeResourceExtension.class);

    private static final String ENTITY_NAME = "employee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmployeeServiceExtension employeeServiceExtension;

    public EmployeeResourceExtension(EmployeeServiceExtension employeeServiceExtension) {
        this.employeeServiceExtension = employeeServiceExtension;
    }

    /**
     * {@code POST  /v1/employees} : Create a new employee using extended service.
     *
     * @param employeeDTO the employeeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new employeeDTO.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */

    @PostMapping("")
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Employee (v1) : {}", employeeDTO);
        if (employeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new employee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        employeeDTO = employeeServiceExtension.save(employeeDTO);
        return ResponseEntity.created(new URI("/api/v1/employees/" + employeeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, employeeDTO.getId().toString()))
            .body(employeeDTO);
    }
}
