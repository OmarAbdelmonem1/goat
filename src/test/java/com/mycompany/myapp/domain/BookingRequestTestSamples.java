package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BookingRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BookingRequest getBookingRequestSample1() {
        return new BookingRequest().id(1L).purpose("purpose1");
    }

    public static BookingRequest getBookingRequestSample2() {
        return new BookingRequest().id(2L).purpose("purpose2");
    }

    public static BookingRequest getBookingRequestRandomSampleGenerator() {
        return new BookingRequest().id(longCount.incrementAndGet()).purpose(UUID.randomUUID().toString());
    }
}
