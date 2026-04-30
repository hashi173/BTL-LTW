package com.coffeeshop;

import com.coffeeshop.entity.Role;
import com.coffeeshop.entity.User;
import com.coffeeshop.repository.UserRepository;
import com.coffeeshop.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void inactiveUserIsLoadedAsDisabled() {
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("inactive-user");
        user.setPassword("secret");
        user.setFullName("Inactive User");
        user.setRole(Role.STAFF);
        user.setUserCode("S900");
        user.setPhone("0909999999");
        user.setActive(false);
        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("inactive-user");

        assertFalse(userDetails.isEnabled());
    }
}
