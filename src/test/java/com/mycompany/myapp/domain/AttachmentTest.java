package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AttachmentTestSamples.*;
import static com.mycompany.myapp.domain.VacationRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attachment.class);
        Attachment attachment1 = getAttachmentSample1();
        Attachment attachment2 = new Attachment();
        assertThat(attachment1).isNotEqualTo(attachment2);

        attachment2.setId(attachment1.getId());
        assertThat(attachment1).isEqualTo(attachment2);

        attachment2 = getAttachmentSample2();
        assertThat(attachment1).isNotEqualTo(attachment2);
    }

    @Test
    void vacationRequestTest() {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        VacationRequest vacationRequestBack = getVacationRequestRandomSampleGenerator();

        attachment.setVacationRequest(vacationRequestBack);
        assertThat(attachment.getVacationRequest()).isEqualTo(vacationRequestBack);

        attachment.vacationRequest(null);
        assertThat(attachment.getVacationRequest()).isNull();
    }
}
