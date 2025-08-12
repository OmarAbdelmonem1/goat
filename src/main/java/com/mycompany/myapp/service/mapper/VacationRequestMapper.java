package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.VacationRequest;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.service.dto.VacationRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VacationRequest} and its DTO {@link VacationRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface VacationRequestMapper extends EntityMapper<VacationRequestDTO, VacationRequest> {
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeId")
    VacationRequestDTO toDto(VacationRequest s);

    @Named("employeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EmployeeDTO toDtoEmployeeId(Employee employee);
}
