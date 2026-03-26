package com.ethan.byztine.controller;

import org.springframework.web.bind.annotation.*;

import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.event.CombatEvent;
import com.ethan.byztine.domain.event.TrainingEvent;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@RestController
@RequestMapping("/api/debug")
public class DebugApiController {

    private final UserRepository userRepository;
    private final CombatEngine combatEngine;

    public DebugApiController(
            UserRepository userRepository,
            CombatEngine combatEngine) {

        this.userRepository = userRepository;
        this.combatEngine = combatEngine;
    }

    @PostMapping("/create-user")
    public String createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        if (username == null || username.isBlank()) {
            return "Username cannot be empty";
        }

        if (email == null || email.isBlank()) {
            return "Email cannot be empty";
        }

        if (password == null || password.isBlank()) {
            return "Password cannot be empty";
        }

        var existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return "User already exists with email: " + email;
        }

        // ⚠️ DEBUG: sin encriptar (esto ya lo sabes)
        String passwordHash = password;

        User user = new User(username, email, passwordHash);
        userRepository.save(user);

        return "User created: " + username + " (" + email + ")";
    }

    @PostMapping("/create-character")
    public String createCharacter(@RequestParam String email,
            @RequestParam String name) {

        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        if (user.getCharacter() != null) {
            return "User already has a character";
        }

        var character = new com.ethan.byztine.domain.Character(name);

        user.assignCharacter(character);
        userRepository.save(user);

        return "Character created: " + name;
    }

    @PostMapping("/gain-xp")
    public String gainXp(@RequestParam String email,
            @RequestParam int amount) {

        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        if (user.getCharacter() == null) {
            return "User has no character";
        }

        var character = user.getCharacter();

        try {
            character.gainExperience(amount);
        } catch (Exception e) {
            return "Error gaining XP: " + e.getMessage();
        }

        userRepository.save(user);

        return "Gained " + amount + " XP. Current level: "
                + character.getCurrentLevel();

    }

    @PostMapping("/restore-energy")
    public String restoreEnergy(@RequestParam String email) {

        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        var user = optionalUser.get();

        if (user.getCharacter() == null) {
            return "User has no character";
        }

        var character = user.getCharacter();

        character.getEnergy().regenerate(character.getEnergy().getMaxEnergy());

        userRepository.save(user);

        return "Energy fully restored";
    }

    @PostMapping("/add-gold")
    public String addGold(@RequestParam String email,
            @RequestParam int amount) {

        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        var user = optionalUser.get();

        if (user.getCharacter() == null) {
            return "User has no character";
        }

        var character = user.getCharacter();

        character.addGold(amount);

        userRepository.save(user);

        return "Added " + amount + " gold";
    }

    @PostMapping("/add-strength")
    public String addStrength(@RequestParam String email,
            @RequestParam int amount) {

        var user = userRepository.findByEmail(email).orElse(null);

        if (user == null)
            return "User not found";
        if (user.getCharacter() == null)
            return "No character";

        var c = user.getCharacter();

        c.getStats().increaseStrength(amount);

        userRepository.save(user);

        return "Added " + amount + " STR";
    }

    @PostMapping("/add-intelligence")
    public String addIntelligence(@RequestParam String email,
            @RequestParam int amount) {

        var user = userRepository.findByEmail(email).orElse(null);

        if (user == null)
            return "User not found";
        if (user.getCharacter() == null)
            return "No character";

        var c = user.getCharacter();

        c.getStats().increaseIntelligence(amount);

        userRepository.save(user);

        return "Added " + amount + " INT";
    }

    @PostMapping("/add-agility")
    public String addAgility(@RequestParam String email,
            @RequestParam int amount) {

        var user = userRepository.findByEmail(email).orElse(null);

        if (user == null)
            return "User not found";
        if (user.getCharacter() == null)
            return "No character";

        var c = user.getCharacter();

        c.getStats().increaseAgility(amount);

        userRepository.save(user);

        return "Added " + amount + " AGI";
    }

    @PostMapping("/train")
    public String train(@RequestParam String email,
            @RequestParam String stat) {

        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty())
            return "User not found";

        var user = optionalUser.get();

        if (user.getCharacter() == null)
            return "User has no character";

        var character = user.getCharacter();

        try {
            var result = character.executeEvent(
                    new TrainingEvent(stat));

            userRepository.save(user);

            return "🏋️ Training (" + stat.toUpperCase() + ")\n" + result.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/combat")
    public String combat(
            @RequestParam String attackerEmail,
            @RequestParam String defenderEmail) {

        var attackerOpt = userRepository.findByEmail(attackerEmail);
        var defenderOpt = userRepository.findByEmail(defenderEmail);

        if (attackerOpt.isEmpty())
            return "Attacker not found";

        if (defenderOpt.isEmpty())
            return "Defender not found";

        var attackerUser = attackerOpt.get();
        var defenderUser = defenderOpt.get();

        if (attackerUser.getCharacter() == null)
            return "Attacker has no character";

        if (defenderUser.getCharacter() == null)
            return "Defender has no character";

        var attacker = attackerUser.getCharacter();
        var defender = defenderUser.getCharacter();

        try {
            var event = new CombatEvent(combatEngine, defender);

            var result = attacker.executeEvent(event);

            userRepository.save(attackerUser);

            boolean win = result.getGoldGained() > 0;
            return "⚔️ Combat\n\n" +
                    attacker.getName() + " vs " + defender.getName() + "\n\n" +
                    (win ? "🏆 Victory\n\n" : "💀 Defeat\n\n") +
                    result.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/user")
    public String getUser(@RequestParam String email) {

        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        return "Found user: " + user.getUsername() + " (" + user.getEmail() + ")";
    }

    @GetMapping("/character")
    public String getCharacter(@RequestParam String email) {

        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        if (user.getCharacter() == null) {
            return "User has no character";
        }

        var c = user.getCharacter();

        return "👤 " + c.getName() +
                "\nLevel: " + c.getCurrentLevel() +
                "\nXP: " + c.getCurrentExperience() +
                "\nEnergy: " + c.getEnergy().getCurrentEnergy() + " / " + c.getEnergy().getMaxEnergy() +
                "\nGold: " + c.getGold() +
                "\nSTR: " + c.getStats().getStrength() +
                "\nINT: " + c.getStats().getIntelligence() +
                "\nAGI: " + c.getStats().getAgility();

    }
}