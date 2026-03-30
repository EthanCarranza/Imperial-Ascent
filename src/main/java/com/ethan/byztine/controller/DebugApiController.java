package com.ethan.byztine.controller;

import com.ethan.byztine.application.ExecuteEventService;
import com.ethan.byztine.application.UserService;
import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.event.CombatEvent;
import com.ethan.byztine.domain.event.EventResult;
import com.ethan.byztine.domain.event.TrainingEvent;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/debug")
public class DebugApiController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ExecuteEventService executeEventService;
    private final CombatEngine combatEngine;

    public DebugApiController(
            UserRepository userRepository,
            UserService userService,
            ExecuteEventService executeEventService,
            CombatEngine combatEngine) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.executeEventService = executeEventService;
        this.combatEngine = combatEngine;
    }

    @PostMapping("/create-user")
    public String createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        return handleRequest(() -> {
            userService.register(username, email, password);
            return "User created: " + username + " (" + email + ")";
        });
    }

    @PostMapping("/create-character")
    public String createCharacter(@RequestParam String email,
            @RequestParam String name) {

        return handleRequest(() -> {
            User user = requireUserByEmail(email, "User");

            if (user.getCharacter() != null) {
                throw new IllegalStateException("User already has a character");
            }

            user.assignCharacter(new Character(name));
            userRepository.save(user);

            return "Character created: " + name;
        });
    }

    @PostMapping("/gain-xp")
    public String gainXp(@RequestParam String email,
            @RequestParam int amount) {

        return handleRequest(() -> {
            requirePositiveAmount(amount);
            User user = requireUserByEmail(email, "User");
            Character character = requireCharacter(user, "User");

            character.gainExperience(amount);
            userRepository.save(user);

            return "Gained " + amount + " XP. Current level: "
                    + character.getCurrentLevel();
        });
    }

    @PostMapping("/restore-energy")
    public String restoreEnergy(@RequestParam String email) {

        return handleRequest(() -> {
            User user = requireUserByEmail(email, "User");
            Character character = requireCharacter(user, "User");

            character.getEnergy().regenerate(character.getEnergy().getMaxEnergy());
            userRepository.save(user);

            return "Energy fully restored";
        });
    }

    @PostMapping("/add-gold")
    public String addGold(@RequestParam String email,
            @RequestParam int amount) {

        return handleRequest(() -> {
            requirePositiveAmount(amount);
            User user = requireUserByEmail(email, "User");
            Character character = requireCharacter(user, "User");

            character.addGold(amount);
            userRepository.save(user);

            return "Added " + amount + " gold";
        });
    }

    @PostMapping("/add-strength")
    public String addStrength(@RequestParam String email,
            @RequestParam int amount) {

        return updateStat(email, amount, "STR",
                character -> character.getStats().increaseStrength(amount));
    }

    @PostMapping("/add-intelligence")
    public String addIntelligence(@RequestParam String email,
            @RequestParam int amount) {

        return updateStat(email, amount, "INT",
                character -> character.getStats().increaseIntelligence(amount));
    }

    @PostMapping("/add-agility")
    public String addAgility(@RequestParam String email,
            @RequestParam int amount) {

        return updateStat(email, amount, "AGI",
                character -> character.getStats().increaseAgility(amount));
    }

    @PostMapping("/add-luck")
    public String addLuck(@RequestParam String email,
            @RequestParam int amount) {

        return updateStat(email, amount, "LUCK",
                character -> character.getStats().increaseLuck(amount));
    }

    @PostMapping("/train")
    public String train(@RequestParam String email,
            @RequestParam String stat) {

        return handleRequest(() -> {
            User user = requireUserByEmail(email, "User");
            requireCharacter(user, "User");

            EventResult result = executeEventService.executeEvent(
                    user.getId(),
                    new TrainingEvent(stat));

            return "Training (" + formatStat(stat) + ")\n" + result;
        });
    }

    @PostMapping("/combat")
    public String combat(
            @RequestParam String attackerEmail,
            @RequestParam String defenderEmail) {

        return handleRequest(() -> {
            User attackerUser = requireUserByEmail(attackerEmail, "Attacker");
            User defenderUser = requireUserByEmail(defenderEmail, "Defender");
            Character attacker = requireCharacter(attackerUser, "Attacker");
            Character defender = requireCharacter(defenderUser, "Defender");

            EventResult result = executeEventService.executeEvent(
                    attackerUser.getId(),
                    new CombatEvent(combatEngine, defender));

            boolean win = result.getGoldGained() > 0;
            return "Combat\n\n" +
                    attacker.getName() + " vs " + defender.getName() + "\n\n" +
                    (win ? "Victory\n\n" : "Defeat\n\n") +
                    result;
        });
    }

    @GetMapping("/user")
    public String getUser(@RequestParam String email) {

        return handleRequest(() -> {
            User user = requireUserByEmail(email, "User");
            return "Found user: " + user.getUsername() + " (" + user.getEmail() + ")";
        });
    }

    @GetMapping("/character")
    public String getCharacter(@RequestParam String email) {

        return handleRequest(() -> {
            Character character = requireCharacter(
                    requireUserByEmail(email, "User"),
                    "User");

            return character.getName() +
                    "\nLevel: " + character.getCurrentLevel() +
                    "\nXP: " + character.getCurrentExperience() +
                    "\nEnergy: " + character.getEnergy().getCurrentEnergy() + " / "
                    + character.getEnergy().getMaxEnergy() +
                    "\nGold: " + character.getGold() +
                    "\nSTR: " + character.getStats().getStrength() +
                    "\nINT: " + character.getStats().getIntelligence() +
                    "\nAGI: " + character.getStats().getAgility() +
                    "\nLUCK: " + character.getStats().getLuck();
        });
    }

    private String updateStat(String email,
            int amount,
            String label,
            Consumer<Character> updateAction) {

        return handleRequest(() -> {
            requirePositiveAmount(amount);
            User user = requireUserByEmail(email, "User");
            Character character = requireCharacter(user, "User");

            updateAction.accept(character);
            userRepository.save(user);

            return "Added " + amount + " " + label;
        });
    }

    private User requireUserByEmail(String email, String subject) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(subject + " not found"));
    }

    private Character requireCharacter(User user, String subject) {
        Character character = user.getCharacter();

        if (character == null) {
            throw new IllegalStateException(subject + " has no character");
        }

        return character;
    }

    private void requirePositiveAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private String handleRequest(Supplier<String> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

    private String formatStat(String stat) {
        if (stat == null) {
            return "";
        }

        return stat.trim().toUpperCase(Locale.ROOT);
    }
}
