package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.DepartmentType;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.service.mapper.EmployeeMapper;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceExtension extends EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceExtension(
        EmployeeRepository employeeRepository,
        EmployeeMapper employeeMapper,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        super(employeeRepository, employeeMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public EmployeeDTO save(EmployeeDTO employeeDTO) {
        LOG.debug("Request to save Employee : {}", employeeDTO);

        // Map DTO to Entity
        Employee employee = employeeMapper.toEntity(employeeDTO);

        // 1. Create new User
        User user = new User();
        user.setLogin(generateLoginFromName(employee.getName()));
        user.setFirstName(employee.getName());
        user.setEmail(employee.getEmail().toLowerCase());
        user.setActivated(true);
        user.setLangKey("en");

        // Default password
        String defaultPassword = "123456"; //
        user.setPassword(passwordEncoder.encode(defaultPassword));

        // Authorities
        // Assign authorities based on department role
        Set<Authority> authorities = new HashSet<>();
        Authority roleUser = new Authority();
        roleUser.setName("ROLE_USER");
        authorities.add(roleUser);

        if (employee.getUserRole() == DepartmentType.HR) {
            Authority roleAdmin = new Authority();
            roleAdmin.setName("ROLE_ADMIN");
            authorities.add(roleAdmin);
        }

        user.setAuthorities(authorities);

        // Save User
        user = userRepository.save(user);

        // 2. Link User to Employee
        employee.setUser(user);

        // 3. Save Employee
        employee = employeeRepository.save(employee);

        return employeeMapper.toDto(employee);
    }

    private String generateLoginFromName(String name) {
        return name.trim().toLowerCase().replace(" ", ".");
    }
}
