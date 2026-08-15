package com.booknest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "borrowRequest")
public class BorrowRequest {

    @Id
    private String id;

    private String bookId;
    private String bookTitle;
    private String ownerEmail;
    private String borrowerName;
    private String borrowerEmail;
    private String status; // "pending", "Approved", "Rejected", etc.

    @CreatedDate
    private LocalDateTime requestDate;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
