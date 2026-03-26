package com.ethan.byztine.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@Configuration
@Profile("dev")
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(UserRepository userRepository) {
        return args -> {

            // ⚠️ Evita duplicar si ya existe seed
            if (userRepository.findByEmail("warrior@test.com").isPresent()) {
                System.out.println("🌱 Seed already exists");
                return;
            }

            // =========================
            // 🛡️ WARRIOR (STR alto)
            // =========================
            User warrior = new User("Warrior", "warrior@test.com", "1234");
            Character warriorChar = new Character("Thorgar");

            warriorChar.getStats().increaseStrength(5);
            warriorChar.getStats().increaseAgility(2);
            warriorChar.getStats().increaseIntelligence(1);

            warriorChar.addGold(100);

            warrior.assignCharacter(warriorChar);
            userRepository.save(warrior);

            // =========================
            // 🧠 MAGE (INT alto)
            // =========================
            User mage = new User("Mage", "mage@test.com", "1234");
            Character mageChar = new Character("Eldrin");

            mageChar.getStats().increaseStrength(1);
            mageChar.getStats().increaseAgility(2);
            mageChar.getStats().increaseIntelligence(6);

            mageChar.addGold(50);

            mage.assignCharacter(mageChar);
            userRepository.save(mage);

            // =========================
            // ⚡ ROGUE (AGI alto)
            // =========================
            User rogue = new User("Rogue", "rogue@test.com", "1234");
            Character rogueChar = new Character("Shade");

            rogueChar.getStats().increaseStrength(2);
            rogueChar.getStats().increaseAgility(6);
            rogueChar.getStats().increaseIntelligence(1);

            rogueChar.addGold(75);

            rogue.assignCharacter(rogueChar);
            userRepository.save(rogue);

            // =========================
            // ⚖️ BALANCED
            // =========================
            User balanced = new User("Balanced", "balanced@test.com", "1234");
            Character balancedChar = new Character("Aurelian");

            balancedChar.getStats().increaseStrength(3);
            balancedChar.getStats().increaseAgility(3);
            balancedChar.getStats().increaseIntelligence(3);

            balancedChar.addGold(60);

            balanced.assignCharacter(balancedChar);
            userRepository.save(balanced);

            // =========================
            // 🧪 TEST DUMMY (débil)
            // =========================
            User dummy = new User("Dummy", "dummy@test.com", "1234");
            Character dummyChar = new Character("Training Dummy");

            dummyChar.getStats().increaseStrength(1);
            dummyChar.getStats().increaseAgility(1);
            dummyChar.getStats().increaseIntelligence(1);

            dummyChar.addGold(10);

            dummy.assignCharacter(dummyChar);
            userRepository.save(dummy);

            System.out.println("🌱 Database seeded with test characters");
        };
    }
}