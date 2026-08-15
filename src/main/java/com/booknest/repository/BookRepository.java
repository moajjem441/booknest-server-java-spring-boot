package com.booknest.repository;

import com.booknest.model.Book;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    long countByOwnerId(String ownerId);
    List<Book> findByOwnerId(String ownerId);
    List<Book> findByIdIn(List<String> ids);

    @Query("{ '$or': [ { 'ownerId': ?0 }, { 'ownerId': ?1 } ] }")
    List<Book> findByOwnerIdOrOwnerIdObjectId(String ownerIdStr, ObjectId ownerIdObj);

    @Query(value = "{ '$or': [ { 'ownerId': ?0 }, { 'ownerId': ?1 } ] }", count = true)
    long countByOwnerIdOrOwnerIdObjectId(String ownerIdStr, ObjectId ownerIdObj);
}
