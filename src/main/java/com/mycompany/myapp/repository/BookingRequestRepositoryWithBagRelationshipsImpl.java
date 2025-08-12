package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BookingRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class BookingRequestRepositoryWithBagRelationshipsImpl implements BookingRequestRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String BOOKINGREQUESTS_PARAMETER = "bookingRequests";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<BookingRequest> fetchBagRelationships(Optional<BookingRequest> bookingRequest) {
        return bookingRequest.map(this::fetchInvitedUsers);
    }

    @Override
    public Page<BookingRequest> fetchBagRelationships(Page<BookingRequest> bookingRequests) {
        return new PageImpl<>(
            fetchBagRelationships(bookingRequests.getContent()),
            bookingRequests.getPageable(),
            bookingRequests.getTotalElements()
        );
    }

    @Override
    public List<BookingRequest> fetchBagRelationships(List<BookingRequest> bookingRequests) {
        return Optional.of(bookingRequests).map(this::fetchInvitedUsers).orElse(Collections.emptyList());
    }

    BookingRequest fetchInvitedUsers(BookingRequest result) {
        return entityManager
            .createQuery(
                "select bookingRequest from BookingRequest bookingRequest left join fetch bookingRequest.invitedUsers where bookingRequest.id = :id",
                BookingRequest.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<BookingRequest> fetchInvitedUsers(List<BookingRequest> bookingRequests) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, bookingRequests.size()).forEach(index -> order.put(bookingRequests.get(index).getId(), index));
        List<BookingRequest> result = entityManager
            .createQuery(
                "select bookingRequest from BookingRequest bookingRequest left join fetch bookingRequest.invitedUsers where bookingRequest in :bookingRequests",
                BookingRequest.class
            )
            .setParameter(BOOKINGREQUESTS_PARAMETER, bookingRequests)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
