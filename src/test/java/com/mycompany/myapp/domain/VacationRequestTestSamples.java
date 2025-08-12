package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class VacationRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static VacationRequest getVacationRequestSample1() {
        return new VacationRequest().id(1L);
    }

    public static VacationRequest getVacationRequestSample2() {
        return new VacationRequest().id(2L);
    }

    public static VacationRequest getVacationRequestRandomSampleGenerator() {
        return new VacationRequest().id(longCount.incrementAndGet());
    }
}
