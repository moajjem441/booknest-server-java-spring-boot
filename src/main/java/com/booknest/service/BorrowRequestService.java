package com.booknest.service;

import com.booknest.exception.BadRequestException;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.model.Book;
import com.booknest.model.BorrowRequest;
import com.booknest.repository.BookRepository;
import com.booknest.repository.BorrowRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowRequestService {

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    public List<BorrowRequest> getAllBorrowRequests() {
        return borrowRequestRepository.findAll();
    }

    public BorrowRequest createBorrowRequest(String bookId, String borrowerName, String borrowerEmail) {
        if (!bookService.isValidObjectId(bookId)) {
            throw new BadRequestException("Invalid Book ID");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (borrowerEmail != null && borrowerEmail.equals(book.getOwnerEmail())) {
            throw new BadRequestException("You can't borrow your own book.");
        }

        // Check if already requested and pending
        boolean alreadyRequested = borrowRequestRepository
                .findByBookIdAndBorrowerEmailAndStatus(bookId, borrowerEmail, "pending")
                .isPresent();

        if (alreadyRequested) {
            throw new BadRequestException("You already requested this book.");
        }

        BorrowRequest request = BorrowRequest.builder()
                .bookId(bookId)
                .bookTitle(book.getTitle())
                .ownerEmail(book.getOwnerEmail() != null ? book.getOwnerEmail() : "")
                .borrowerName(borrowerName)
                .borrowerEmail(borrowerEmail)
                .status("pending")
                .requestDate(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return borrowRequestRepository.save(request);
    }

    public List<BorrowRequest> getBorrowRequestsByEmail(String email) {
        return borrowRequestRepository.findByBorrowerEmailOrderByRequestDateDesc(email);
    }

    public List<BorrowRequest> getPendingBorrowRequestsByEmail(String email) {
        return borrowRequestRepository.findByBorrowerEmailAndStatusOrderByRequestDateDesc(email, "pending");
    }

    public void deleteBorrowRequest(String id, String invalidIdMessage) {
        if (!bookService.isValidObjectId(id)) {
            throw new BadRequestException(invalidIdMessage);
        }
        if (!borrowRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Borrow request not found.");
        }
        borrowRequestRepository.deleteById(id);
    }

    public void updateBorrowRequestStatus(String id, String status) {
        if (!bookService.isValidObjectId(id)) {
            throw new BadRequestException("Invalid request ID format.");
        }

        List<String> allowedStatuses = Arrays.asList("Approved", "Rejected", "Pending");
        if (status == null || !allowedStatuses.contains(status)) {
            throw new BadRequestException("Invalid status value provided.");
        }

        BorrowRequest request = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found."));

        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());
        borrowRequestRepository.save(request);

        if ("Approved".equals(status) && request.getBookId() != null) {
            if (bookService.isValidObjectId(request.getBookId())) {
                Book book = bookRepository.findById(request.getBookId()).orElse(null);
                if (book != null) {
                    book.setStatus("Borrowed");
                    book.setUpdatedAt(LocalDateTime.now());
                    bookRepository.save(book);
                }
            }
        }
    }

    public List<Book> getBorrowedBooksByEmail(String email) {
        List<BorrowRequest> approvedRequests = borrowRequestRepository
                .findByBorrowerEmailAndStatus(email, "Approved");

        List<String> bookIds = approvedRequests.stream()
                .map(BorrowRequest::getBookId)
                .filter(bookService::isValidObjectId)
                .collect(Collectors.toList());

        return bookRepository.findByIdIn(bookIds);
    }
}
