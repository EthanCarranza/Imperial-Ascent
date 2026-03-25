package com.ethan.byztine.controller;

import org.springframework.web.bind.annotation.*;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@RestController
@RequestMapping("/api/debug")
public class DebugApiController {

    private final UserRepository userRepository;

    public DebugApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                + character.getLevel().getCurrentLevel();

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

    @PostMapping("/train")
    public String train(@RequestParam String email) {

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
            var result = character.executeEvent(
                    new com.ethan.byztine.domain.event.TrainingEvent());

            userRepository.save(user);

            return "🏋️ Training | XP: +" + result.getExperienceGained()
                    + " | Gold: +" + result.getGoldGained()
                    + " | Level: " + result.getLevelBefore()
                    + " → " + result.getLevelAfter();

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
                "\nGold: " + c.getGold();
    }
}