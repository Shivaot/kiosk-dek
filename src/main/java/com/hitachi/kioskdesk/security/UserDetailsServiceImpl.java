package com.hitachi.kioskdesk.security;

import com.hitachi.kioskdesk.domain.User;
import com.hitachi.kioskdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Shiva Created on 31/10/21
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) throw new UsernameNotFoundException("Could not find user with username + " + username);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return userDetails;
    }

}
