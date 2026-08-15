package com.booknest.repository;

import com.booknest.model.BorrowRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRequestRepository extends MongoRepository<BorrowRequest, String> {
    List<BorrowRequest> findByBorrowerEmail(String borrowerEmail);
    List<BorrowRequest> findByBorrowerEmailAndStatus(String borrowerEmail, String status);
    Optional<BorrowRequest> findByBookIdAndBorrowerEmailAndStatus(String bookId, String borrowerEmail, String status);
    
    List<BorrowRequest> findByBorrowerEmailOrderByRequestDateDesc(String borrowerEmail);
    List<BorrowRequest> findByBorrowerEmailAndStatusOrderByRequestDateDesc(String borrowerEmail, String status);
}
