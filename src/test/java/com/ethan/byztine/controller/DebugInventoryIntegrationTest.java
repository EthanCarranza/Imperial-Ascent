package com.ethan.byztine.controller;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.inventory.EquipmentSlot;
import com.ethan.byztine.domain.inventory.InventoryItem;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DebugInventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void characterSheetShouldLoadInventoryWithoutLazyInitializationFailure() throws Exception {
        User user = new User("theodora", "theodora-inventory@test.com", "hash");
        Character character = new Character("Theodora");
        InventoryItem sword = new InventoryItem("Sword", EquipmentSlot.WEAPON, 40, 2, 0, 0, 0, 2, 0);

        character.addItem(sword);
        character.equipItem(sword.getId());
        user.assignCharacter(character);
        userRepository.save(user);

        mockMvc.perform(get("/api/debug/character-sheet")
                .param("email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventoryUsage").value(1))
                .andExpect(jsonPath("$.weaponDamageBonus").value(2))
                .andExpect(jsonPath("$.equipmentSlots[0].itemName").value("Sword"))
                .andExpect(jsonPath("$.equipmentSlots[0].weaponDamage").value(2))
                .andExpect(jsonPath("$.inventoryItems[0].name").value("Sword"));
    }
}
