package com.ethan.byztine.controller;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.inventory.InventoryItem;
import com.ethan.byztine.domain.inventory.ItemPreset;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DebugShopIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shopOffersShouldExposeVendorStockForAllowedLevel() throws Exception {
        User user = new User("theodora", "theodora-shop@test.com", "hash");
        Character character = new Character("Theodora");
        levelUpTo(character, 3);
        character.addGold(200);
        user.assignCharacter(character);
        userRepository.save(user);

        InventoryItem sword = InventoryItem.fromPreset(ItemPreset.TRAINING_SWORD, 2);
        int expectedPrice = (int) Math.ceil(sword.getGoldValue() * 1.35);

        mockMvc.perform(get("/api/debug/shop-offers")
                .param("email", user.getEmail())
                .param("level", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.characterLevel").value(3))
                .andExpect(jsonPath("$.selectedLevel").value(2))
                .andExpect(jsonPath("$.gold").value(200))
                .andExpect(jsonPath("$.offers[0].presetId").value("training-sword"))
                .andExpect(jsonPath("$.offers[0].rarityDisplayName").value("Common"))
                .andExpect(jsonPath("$.offers[0].level").value(2))
                .andExpect(jsonPath("$.offers[0].weaponDamage").value(3))
                .andExpect(jsonPath("$.offers[0].price").value(expectedPrice))
                .andExpect(jsonPath("$.offers[0].affordable").value(true));
    }

    @Test
    void shopOffersShouldRejectLevelsAboveCharacterLevel() throws Exception {
        User user = new User("romanos", "romanos-shop@test.com", "hash");
        Character character = new Character("Romanos");
        levelUpTo(character, 2);
        user.assignCharacter(character);
        userRepository.save(user);

        mockMvc.perform(get("/api/debug/shop-offers")
                .param("email", user.getEmail())
                .param("level", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Shop level cannot exceed character level"));
    }

    @Test
    void buyItemShouldPersistPurchaseAndDeductGold() throws Exception {
        User user = new User("basil", "basil-shop@test.com", "hash");
        Character character = new Character("Basil");
        levelUpTo(character, 2);
        character.addGold(200);
        user.assignCharacter(character);
        userRepository.save(user);

        InventoryItem sword = InventoryItem.fromPreset(ItemPreset.TRAINING_SWORD, 2);
        int expectedPrice = (int) Math.ceil(sword.getGoldValue() * 1.35);

        mockMvc.perform(post("/api/debug/buy-item")
                .param("email", user.getEmail())
                .param("preset", "training-sword")
                .param("level", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Purchased item: Training Sword [Weapon] Common Lv.2 for "
                                + expectedPrice + " gold. Gold left: " + (200 - expectedPrice)));

        User reloaded = userRepository.findByEmail(user.getEmail()).orElseThrow();

        assertEquals(200 - expectedPrice, reloaded.getCharacter().getGold());
        assertEquals(1, reloaded.getCharacter().getInventoryUsage());
        assertEquals("Training Sword", reloaded.getCharacter().getInventoryItems().get(0).getName());
        assertEquals("Common", reloaded.getCharacter().getInventoryItems().get(0).getRarity().getDisplayName());
        assertEquals(2, reloaded.getCharacter().getInventoryItems().get(0).getLevel());
        assertTrue(reloaded.getCharacter().getInventoryItems().stream().noneMatch(InventoryItem::isEquipped));
    }

    @Test
    void sellItemShouldRemoveItAndAddGoldBack() throws Exception {
        User user = new User("leo", "leo-shop@test.com", "hash");
        Character character = new Character("Leo");
        InventoryItem sword = InventoryItem.fromPreset(ItemPreset.TRAINING_SWORD, 2);
        character.addItem(sword);
        character.equipItem(sword.getId());
        user.assignCharacter(character);
        userRepository.save(user);

        mockMvc.perform(post("/api/debug/sell-item")
                .param("email", user.getEmail())
                .param("itemId", sword.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Sold item: Training Sword [Weapon] Common Lv.2 for "
                                + sword.getGoldValue() + " gold. Gold now: " + sword.getGoldValue()));

        User reloaded = userRepository.findByEmail(user.getEmail()).orElseThrow();

        assertEquals(sword.getGoldValue(), reloaded.getCharacter().getGold());
        assertEquals(0, reloaded.getCharacter().getInventoryUsage());
        assertTrue(reloaded.getCharacter().getEquippedItem(ItemPreset.TRAINING_SWORD.getSlot()).isEmpty());
    }

    @Test
    void destroyItemShouldRemoveItWithoutChangingGold() throws Exception {
        User user = new User("anna", "anna-shop@test.com", "hash");
        Character character = new Character("Anna");
        character.addGold(40);
        InventoryItem ring = InventoryItem.fromPreset(ItemPreset.PORPHYRY_RING, 1);
        character.addItem(ring);
        user.assignCharacter(character);
        userRepository.save(user);

        mockMvc.perform(post("/api/debug/destroy-item")
                .param("email", user.getEmail())
                .param("itemId", ring.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Destroyed item: Porphyry Ring [Ring] Common Lv.1. Gold unchanged: 40"));

        User reloaded = userRepository.findByEmail(user.getEmail()).orElseThrow();

        assertEquals(40, reloaded.getCharacter().getGold());
        assertEquals(0, reloaded.getCharacter().getInventoryUsage());
    }

    private void levelUpTo(Character character, int targetLevel) {
        int totalExperience = 0;

        for (int level = 1; level < targetLevel; level++) {
            totalExperience += 100 * level;
        }

        character.gainExperience(totalExperience);
    }
}
