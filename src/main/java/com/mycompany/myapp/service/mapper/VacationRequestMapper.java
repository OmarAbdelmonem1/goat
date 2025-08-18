package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Attachment;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.VacationRequest;
import com.mycompany.myapp.service.dto.AttachmentDTO;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.service.dto.VacationRequestDTO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VacationRequestMapper extends EntityMapper<VacationRequestDTO, VacationRequest> {
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeId")
    @Mapping(target = "attachments", qualifiedByName = "attachmentsWithoutVacationRequest")
    VacationRequestDTO toDto(VacationRequest vacationRequest);

    @Named("employeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EmployeeDTO toDtoEmployeeId(Employee employee);

    @Named("attachmentsWithoutVacationRequest")
    default List<AttachmentDTO> attachmentsWithoutVacationRequest(Set<Attachment> attachments) {
        if (attachments == null) return null;
        return attachments.stream().map(this::attachmentToAttachmentDTOWithoutVacationRequest).collect(Collectors.toList());
    }

    default AttachmentDTO attachmentToAttachmentDTOWithoutVacationRequest(Attachment attachment) {
        if (attachment == null) return null;
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(attachment.getId());
        dto.setName(attachment.getName());
        dto.setUrl(attachment.getUrl());
        dto.setFileSize(attachment.getFileSize());
        dto.setContentType(attachment.getContentType());
        dto.setUploadedAt(attachment.getUploadedAt());
        // Important: do NOT set dto.setVacationRequest(attachment.getVacationRequest())
        return dto;
    }
}
