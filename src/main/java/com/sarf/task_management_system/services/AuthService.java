package com.sarf.task_management_system.services;

import com.sarf.task_management_system.models.ApplicationUser;
import com.sarf.task_management_system.reositories.ApplicationUserRepository;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private ApplicationUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void addUser(ApplicationUser user) throws EntityExistsException {
        String email = user.getEmail();
        //if (userRepository.findByEmail(email).orElse(null) == null)
            //throw new EntityExistsException("User with email %s already exists".formatted(email));
        user.setHashPassword(passwordEncoder.encode(user.getHashPassword()));
        userRepository.save(user);
    }
}
