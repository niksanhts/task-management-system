package com.sarf.task_management_system.services;

import com.sarf.task_management_system.config.ApplicationUserDetails;
import com.sarf.task_management_system.models.ApplicationUser;
import com.sarf.task_management_system.reositories.ApplicationUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    private ApplicationUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ApplicationUser> user = userRepository.findByEmail(username);
        return user.map(ApplicationUserDetails::new).orElseThrow(
                () -> new UsernameNotFoundException("no user with email: %s".formatted(username))
        );
    }
}
