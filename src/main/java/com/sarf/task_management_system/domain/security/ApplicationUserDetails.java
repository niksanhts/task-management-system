package com.sarf.task_management_system.domain.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ApplicationUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String name;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public ApplicationUserDetails(
            long id,
            String email,
            String name,
            String hashPassword,
            List<GrantedAuthority> grantedAuthorities
    ) {
        this.id = id;
        this.username = email;
        this.password = hashPassword;
        this.authorities = grantedAuthorities;
    }
}
