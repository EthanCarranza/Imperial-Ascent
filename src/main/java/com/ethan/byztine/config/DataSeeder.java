package com.ethan.byztine.config;

import com.ethan.byztine.application.RegistrationService;
import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDatabase(RegistrationService registrationService,
            UserRepository userRepository) {
        return args -> {

            if (userRepository.findByEmail("warrior@test.com").isPresent()) {
                log.info("Seed already exists");
                return;
            }

            seedUser(registrationService, userRepository,
                    "Warrior", "warrior@test.com", "1234", "Thorgar",
                    5, 2, 1, 100);
            seedUser(registrationService, userRepository,
                    "Mage", "mage@test.com", "1234", "Eldrin",
                    1, 2, 6, 50);
            seedUser(registrationService, userRepository,
                    "Rogue", "rogue@test.com", "1234", "Shade",
                    2, 6, 1, 75);
            seedUser(registrationService, userRepository,
                    "Balanced", "balanced@test.com", "1234", "Aurelian",
                    3, 3, 3, 60);
            seedUser(registrationService, userRepository,
                    "Dummy", "dummy@test.com", "1234", "Training Dummy",
                    1, 1, 1, 10);

            log.info("Database seeded with test characters");
        };
    }

    private void seedUser(RegistrationService registrationService,
            UserRepository userRepository,
            String username,
            String email,
            String rawPassword,
            String characterName,
            int strengthBonus,
            int agilityBonus,
            int intelligenceBonus,
            int initialGold) {

        User user = registrationService.registerUserWithCharacter(
                username,
                email,
                rawPassword,
                characterName);

        Character character = user.getCharacter();

        character.getStats().increaseStrength(strengthBonus);
        character.getStats().increaseAgility(agilityBonus);
        character.getStats().increaseIntelligence(intelligenceBonus);
        character.addGold(initialGold);

        userRepository.save(user);
    }
}
