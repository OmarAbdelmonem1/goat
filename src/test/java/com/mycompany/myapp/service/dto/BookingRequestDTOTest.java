package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookingRequestDTO.class);
        BookingRequestDTO bookingRequestDTO1 = new BookingRequestDTO();
        bookingRequestDTO1.setId(1L);
        BookingRequestDTO bookingRequestDTO2 = new BookingRequestDTO();
        assertThat(bookingRequestDTO1).isNotEqualTo(bookingRequestDTO2);
        bookingRequestDTO2.setId(bookingRequestDTO1.getId());
        assertThat(bookingRequestDTO1).isEqualTo(bookingRequestDTO2);
        bookingRequestDTO2.setId(2L);
        assertThat(bookingRequestDTO1).isNotEqualTo(bookingRequestDTO2);
        bookingRequestDTO1.setId(null);
        assertThat(bookingRequestDTO1).isNotEqualTo(bookingRequestDTO2);
    }
}
