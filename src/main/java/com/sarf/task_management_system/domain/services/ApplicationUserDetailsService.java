package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.factories.ApplicationUserDetailsFactory;
import com.sarf.task_management_system.repositories.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    private ApplicationUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            ApplicationUser user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return ApplicationUserDetailsFactory.create(user);
        }
        catch (Exception exception) {
            log.info("Cannot return value. Cause:", exception);
        }
        throw new UsernameNotFoundException("User not found");
    }
}
