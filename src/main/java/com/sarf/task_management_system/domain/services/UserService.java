package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.dto.requsts.RegisterRequest;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.security.JwtTokenProvider;
import com.sarf.task_management_system.repositories.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private ApplicationUserRepository userRepository;
    private JwtTokenProvider tokenProvider;

    public List<ApplicationUser> getAll () {
        return userRepository.findAll();
    }

    public ApplicationUser getById(long id) throws NoSuchElementException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No value present"));
    }

    public ApplicationUser getByEmail(final String email) throws NoSuchElementException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No value present"));
    }

    public ApplicationUser getByToken(final String accessToken) {
        String email= tokenProvider.getEmail(accessToken);
        return this.getByEmail(email);
    }

    public void save(RegisterRequest registerRequest) {
        ApplicationUser user = new ApplicationUser();

        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setRoles(registerRequest.getRoles());
        user.setTasksAssignedToUser(new HashSet<>());
        user.setTasksCreatedByUser(new HashSet<>());
        user.setHashPassword(
                new BCryptPasswordEncoder().encode(
                        registerRequest.getPassword()
                )
        );

        userRepository.save(user);
    }

    public void delete(ApplicationUser user) {
        userRepository.delete(user);
    }

    public void delete(Long id) {
        ApplicationUser user = userRepository.findById(id)
                .orElseThrow();
        delete(user);
    }
}
