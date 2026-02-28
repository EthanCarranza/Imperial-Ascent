package com.ethan.byztine.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ethan.byztine.domain.Character;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserPersistenceTest {

    @Autowired
    private JpaUserRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndRetrieveUser() {

        User user = new User(
                "Marcus",
                "marcus@test.com",
                "hashed-password");

        repository.save(user);

        Optional<User> found = repository.findByEmail("marcus@test.com");

        assertTrue(found.isPresent());
        assertEquals("Marcus", found.get().getUsername());
    }

    @Test
    void shouldPersistUserWithCharacter() {

        User user = new User("marcus", "marcus2@test.com", "hash123");
        Character character = new Character("Aurelius");

        user.assignCharacter(character);

        userRepository.save(user);

        Optional<User> retrieved = userRepository.findByEmail("marcus2@test.com");

        assertTrue(retrieved.isPresent());

        User loaded = retrieved.get();

        assertNotNull(loaded.getCharacter());
        assertEquals("Aurelius", loaded.getCharacter().getName());
        assertEquals(1, loaded.getCharacter().getLevel().getCurrentLevel());
        assertEquals(10, loaded.getCharacter().getEnergy().getCurrentEnergy());
    }
}