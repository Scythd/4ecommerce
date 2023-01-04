package com.moklyak.ecommerce.configurations;


import com.moklyak.ecommerce.entities.Role;
import com.moklyak.ecommerce.entities.User;
import com.moklyak.ecommerce.repositories.RoleRepository;
import com.moklyak.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class UserRoleDataConfiguration  implements ApplicationListener<ContextRefreshedEvent> {

        boolean alreadySetup = false;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void onApplicationEvent(ContextRefreshedEvent event) {

            if (alreadySetup)
                return;

            createRoleIfNotFound("ROLE_ADMIN");
            createRoleIfNotFound("ROLE_USER");

            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            Role userRole = roleRepository.findByName("ROLE_USER");
            User user = userRepository.findByUsername("admin");
            if (user == null) {
                user = new User();
                user.setUsername("admin");
                user.setPassword(passwordEncoder.encode("admin"));
                user.setEmail("a@a.a");
                user.setRoles(List.of(adminRole, userRole));
                userRepository.save(user);
            }

            alreadySetup = true;
        }


        @Transactional
        Role createRoleIfNotFound(String name) {

            Role role = roleRepository.findByName(name);
            if (role == null) {
                role = new Role(name);
                role = roleRepository.save(role);
            }
            return role;
        }

}
