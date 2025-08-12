package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Attachment;
import com.mycompany.myapp.domain.VacationRequest;
import com.mycompany.myapp.service.dto.AttachmentDTO;
import com.mycompany.myapp.service.dto.VacationRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Attachment} and its DTO {@link AttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttachmentMapper extends EntityMapper<AttachmentDTO, Attachment> {
    @Mapping(target = "vacationRequest", source = "vacationRequest", qualifiedByName = "vacationRequestId")
    AttachmentDTO toDto(Attachment s);

    @Named("vacationRequestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VacationRequestDTO toDtoVacationRequestId(VacationRequest vacationRequest);
}
