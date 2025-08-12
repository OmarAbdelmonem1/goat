package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BookingRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BookingRequestRepositoryWithBagRelationships {
    Optional<BookingRequest> fetchBagRelationships(Optional<BookingRequest> bookingRequest);

    List<BookingRequest> fetchBagRelationships(List<BookingRequest> bookingRequests);

    Page<BookingRequest> fetchBagRelationships(Page<BookingRequest> bookingRequests);
}
