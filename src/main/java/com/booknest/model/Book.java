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
@Document(collection = "books")
public class Book {

    @Id
    private String id;

    private String title;
    private String author;
    private String description;
    private String category;
    private String type;
    private String coverImage;
    private String pdfUrl;
    private String pickupLocation;
    private String status; // "Available", "Borrowed", etc.
    private String ownerId;
    private String ownerEmail;
    private String borrowedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
