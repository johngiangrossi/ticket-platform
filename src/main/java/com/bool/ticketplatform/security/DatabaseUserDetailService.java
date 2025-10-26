package com.bool.ticketplatform.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bool.ticketplatform.model.User;
import com.bool.ticketplatform.repository.UserRepository;



@Service
public class DatabaseUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // cerco per nome utente
        Optional<User> userOpt = userRepo.findByUsername(username);

        if (userOpt.isPresent()) {
            return new DatabaseUserDetail(userOpt.get());
        } else {
            throw new UsernameNotFoundException("username not found");
        }
    }
}
