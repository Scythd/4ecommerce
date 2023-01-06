package com.moklyak.ecommerce.services;

import com.moklyak.ecommerce.entities.User;
import com.moklyak.ecommerce.repositories.RoleRepository;
import com.moklyak.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messages;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
            /*return new org.springframework.security.core.userdetails.User(
                    " ", " ", true, true, true, true,
                    List.of(roleRepository.findByName("ROLE_USER")));*/
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), true, true, true,
                !user.isBlocked(), user.getRoles());
    }


}