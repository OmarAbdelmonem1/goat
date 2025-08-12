package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.BookingRequestAsserts.*;
import static com.mycompany.myapp.domain.BookingRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingRequestMapperTest {

    private BookingRequestMapper bookingRequestMapper;

    @BeforeEach
    void setUp() {
        bookingRequestMapper = new BookingRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBookingRequestSample1();
        var actual = bookingRequestMapper.toEntity(bookingRequestMapper.toDto(expected));
        assertBookingRequestAllPropertiesEquals(expected, actual);
    }
}
