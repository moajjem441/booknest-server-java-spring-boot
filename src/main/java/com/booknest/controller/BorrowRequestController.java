package com.booknest.controller;

import com.booknest.model.BorrowRequest;
import com.booknest.service.BorrowRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrow-requests")
public class BorrowRequestController {

    @Autowired
    private BorrowRequestService borrowRequestService;

    @GetMapping
    public List<BorrowRequest> getAllBorrowRequests() {
        return borrowRequestService.getAllBorrowRequests();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBorrowRequestStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        
        String status = body.get("status");
        borrowRequestService.updateBorrowRequestStatus(id, status);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Request status updated to " + status + " successfully.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBorrowRequest(@PathVariable String id) {
        borrowRequestService.deleteBorrowRequest(id, "Invalid request ID format.");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Borrow request deleted successfully.");
        return ResponseEntity.ok(response);
    }
}
