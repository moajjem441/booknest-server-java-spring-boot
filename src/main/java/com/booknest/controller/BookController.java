package com.booknest.controller;

import com.booknest.model.Book;
import com.booknest.model.BorrowRequest;
import com.booknest.service.BookService;
import com.booknest.service.BorrowRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final BorrowRequestService borrowRequestService;

    public BookController(BookService bookService, BorrowRequestService borrowRequestService) {
        this.bookService = bookService;
        this.borrowRequestService = borrowRequestService;
    }

    
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<?> createBook(
            @RequestBody Book book,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getClaimAsString("id");
        if (userId == null) {
            userId = jwt.getSubject();
        }
        String userEmail = jwt.getClaimAsString("email");

        Book savedBook = bookService.createBook(book, userId, userEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Book shared!");
        response.put("bookId", savedBook.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/request")
    public ResponseEntity<?> requestBook(
            @PathVariable String id,
            @RequestBody BorrowerDto borrowerDto,
            @AuthenticationPrincipal Jwt jwt) {

        BorrowRequest request = borrowRequestService.createBorrowRequest(
                id,
                borrowerDto.getName(),
                borrowerDto.getEmail()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("insertedId", request.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Book deleted successfully");
        return ResponseEntity.ok(response);
    }

    // DTO class for borrow requests
    public static class BorrowerDto {
        private String name;
        private String email;

        public BorrowerDto() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
