package com.sarf.task_management_system.domain.factories;

import com.sarf.task_management_system.domain.enums.Role;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.security.ApplicationUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationUserDetailsFactory {

    public static ApplicationUserDetails create(final ApplicationUser user) {
        return new ApplicationUserDetails(
               user.getId(),
                user.getEmail(),
                user.getName(),
                user.getHashPassword(),
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles()))
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(final List<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
