package com.booknest.controller;

import com.booknest.model.Book;
import com.booknest.model.BorrowRequest;
import com.booknest.service.BookService;
import com.booknest.service.BorrowRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    
    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowRequestService borrowRequestService;

    @GetMapping("/books")
    public ResponseEntity<?> getSharedBooksCount(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("id");
        if (userId == null) {
            userId = jwt.getSubject();
        }

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: User ID not found in token."));
        }

        long sharedBooksCount = bookService.countSharedBooks(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("sharedBooksCount", sharedBooksCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrowRequests/email")
    public ResponseEntity<?> getBorrowRequestsForLoggedInUser(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: Email not found in token."));
        }

        List<BorrowRequest> borrowRequests = borrowRequestService.getBorrowRequestsByEmail(email);

        long borrowedBooksCount = borrowRequests.stream()
                .filter(req -> "approved".equalsIgnoreCase(req.getStatus()))
                .count();

        long pendingRequestsCount = borrowRequests.stream()
                .filter(req -> "pending".equalsIgnoreCase(req.getStatus()))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("borrowedBooksCount", borrowedBooksCount);
        stats.put("pendingRequestsCount", pendingRequestsCount);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stats", stats);
        response.put("borrowRequests", borrowRequests);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/shared-books/{userId}")
    public ResponseEntity<?> getSharedBooksByUser(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "User ID is required"));
        }
        List<Book> sharedBooks = bookService.getSharedBooks(userId);
        return ResponseEntity.ok(sharedBooks);
    }

    @GetMapping("/borrowRequests/{email}")
    public ResponseEntity<?> getPendingRequestsByEmail(@PathVariable String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email is required"));
        }
        List<BorrowRequest> pendingRequests = borrowRequestService.getPendingBorrowRequestsByEmail(email);
        return ResponseEntity.ok(pendingRequests);
    }

    @DeleteMapping("/borrowRequests/{id}")
    public ResponseEntity<?> deleteBorrowRequest(@PathVariable String id) {
        borrowRequestService.deleteBorrowRequest(id, "Invalid ID format");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Borrow request deleted successfully");
        response.put("deletedCount", 1);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/borrowed/{email}")
    public ResponseEntity<?> getBorrowedBooksByEmail(@PathVariable String email) {
        List<Book> borrowedBooks = borrowRequestService.getBorrowedBooksByEmail(email);
        return ResponseEntity.ok(borrowedBooks);
    }

    @PatchMapping("/books/return/{id}")
    public ResponseEntity<?> returnBook(@PathVariable String id) {
        bookService.returnBook(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Book returned successfully");
        return ResponseEntity.ok(response);
    }
}
