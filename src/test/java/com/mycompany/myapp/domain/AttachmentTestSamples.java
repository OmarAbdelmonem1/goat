package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Attachment getAttachmentSample1() {
        return new Attachment().id(1L).name("name1").url("url1").fileSize(1L).contentType("contentType1");
    }

    public static Attachment getAttachmentSample2() {
        return new Attachment().id(2L).name("name2").url("url2").fileSize(2L).contentType("contentType2");
    }

    public static Attachment getAttachmentRandomSampleGenerator() {
        return new Attachment()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .url(UUID.randomUUID().toString())
            .fileSize(longCount.incrementAndGet())
            .contentType(UUID.randomUUID().toString());
    }
}
