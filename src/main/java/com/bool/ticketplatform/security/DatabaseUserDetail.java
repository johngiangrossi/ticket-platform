package com.bool.ticketplatform.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bool.ticketplatform.model.Role;
import com.bool.ticketplatform.model.User;



public class DatabaseUserDetail implements UserDetails {

    // fields
    private String username;

    private String password;

    private Set<GrantedAuthority> authorities;
    
    
    // costruttore
    public DatabaseUserDetail(User user) {
        
        this.username = user.getUsername();
        this.password = user.getPassword();

        this.authorities = new HashSet<>();

        for (Role role : user.getRoles()) {
            SimpleGrantedAuthority sga = new SimpleGrantedAuthority(role.getRoleName());
            this.authorities.add(sga);            
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
