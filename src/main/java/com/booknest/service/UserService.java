package com.booknest.service;

import com.booknest.exception.BadRequestException;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.model.User;
import com.booknest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(String id) {
        if (!isValidObjectId(id)) {
            throw new BadRequestException("Invalid User ID format");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public boolean isValidObjectId(String id) {
        return id != null && id.matches("^[0-9a-fA-F]{24}$");
    }
}
