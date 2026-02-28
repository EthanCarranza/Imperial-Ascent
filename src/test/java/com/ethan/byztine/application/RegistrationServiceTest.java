package com.ethan.byztine.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@SpringBootTest
@Transactional
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterUserWithCharacter() {

        User user = registrationService.registerUserWithCharacter(
                "alex",
                "alex_unique@test.com",
                "hash123",
                "Leonidas");

        assertNotNull(user.getId());
        assertNotNull(user.getCharacter());
        assertEquals("Leonidas", user.getCharacter().getName());

        assertTrue(userRepository.findByEmail("alex_unique@test.com").isPresent());
    }
}