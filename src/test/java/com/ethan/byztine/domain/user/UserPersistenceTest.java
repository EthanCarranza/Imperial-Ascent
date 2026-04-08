package com.ethan.byztine.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.inventory.EquipmentSlot;
import com.ethan.byztine.domain.inventory.InventoryItem;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndRetrieveUser() {

        User user = new User(
                "Marcus",
                "marcus@test.com",
                "hashed-password");

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("marcus@test.com");

        assertTrue(found.isPresent());
        assertEquals("Marcus", found.get().getUsername());
        assertNotNull(found.get().getId());
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

    @Test
    void shouldPersistCharacterInventoryAndEquipment() {

        User user = new User("marcus", "marcus3@test.com", "hash123");
        Character character = new Character("Aurelius");
        InventoryItem sword = new InventoryItem("Sword", EquipmentSlot.WEAPON, 40, 2, 0, 0, 0);

        character.addItem(sword);
        character.equipItem(sword.getId());
        user.assignCharacter(character);

        userRepository.save(user);

        Optional<User> retrieved = userRepository.findByEmail("marcus3@test.com");

        assertTrue(retrieved.isPresent());
        assertEquals(1, retrieved.get().getCharacter().getInventoryUsage());
        assertEquals(7, retrieved.get().getCharacter().getEffectiveStrength());
        assertTrue(retrieved.get().getCharacter().getEquippedItem(EquipmentSlot.WEAPON).isPresent());
    }
}
