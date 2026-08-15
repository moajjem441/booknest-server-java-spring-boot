package com.booknest.service;

import com.booknest.exception.BadRequestException;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.model.Book;
import com.booknest.repository.BookRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(String id) {
        if (!isValidObjectId(id)) {
            throw new BadRequestException("Invalid Book ID format");
        }
        return bookRepository.findById(id).orElse(null);
    }

    public Book createBook(Book book, String userId, String userEmail) {
        book.setId(null); // Ensure MongoDB generates the ID
        book.setStatus("Available");
        book.setOwnerId(userId);
        
        // If ownerEmail is not provided in body, set it from JWT
        if (book.getOwnerEmail() == null || book.getOwnerEmail().trim().isEmpty()) {
            book.setOwnerEmail(userEmail);
        }
        
        // Handle physical/pdf specific fields
        if ("PDF".equalsIgnoreCase(book.getType())) {
            book.setPickupLocation("");
        } else if ("Physical".equalsIgnoreCase(book.getType())) {
            book.setPdfUrl("");
        }
        
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        
        return bookRepository.save(book);
    }

    public void deleteBook(String id) {
        if (!isValidObjectId(id)) {
            throw new BadRequestException("Invalid book ID format");
        }
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    public long countSharedBooks(String userId) {
        if (isValidObjectId(userId)) {
            return bookRepository.countByOwnerIdOrOwnerIdObjectId(userId, new ObjectId(userId));
        } else {
            return bookRepository.countByOwnerId(userId);
        }
    }

    public List<Book> getSharedBooks(String userId) {
        if (isValidObjectId(userId)) {
            return bookRepository.findByOwnerIdOrOwnerIdObjectId(userId, new ObjectId(userId));
        } else {
            return bookRepository.findByOwnerId(userId);
        }
    }

    public void returnBook(String id) {
        if (!isValidObjectId(id)) {
            throw new BadRequestException("Invalid book ID");
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        
        book.setStatus("available"); // matches Node.js lowercase value
        book.setBorrowedBy(null);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
    }

    public boolean isValidObjectId(String id) {
        return id != null && id.matches("^[0-9a-fA-F]{24}$");
    }
}
