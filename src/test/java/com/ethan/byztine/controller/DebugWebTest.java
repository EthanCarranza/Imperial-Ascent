package com.ethan.byztine.controller;

import com.ethan.byztine.application.ExecuteEventService;
import com.ethan.byztine.application.ShopService;
import com.ethan.byztine.application.UserService;
import com.ethan.byztine.config.SecurityConfig;
import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.combat.CombatReport;
import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.combat.CombatResult;
import com.ethan.byztine.domain.combat.CombatRoundReport;
import com.ethan.byztine.domain.event.CombatEventResult;
import com.ethan.byztine.domain.event.TrainingEvent;
import com.ethan.byztine.domain.inventory.EquipmentSlot;
import com.ethan.byztine.domain.inventory.InventoryItem;
import com.ethan.byztine.domain.inventory.ItemRarity;
import com.ethan.byztine.domain.inventory.ItemValueCalculator;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({ DebugController.class, DebugApiController.class })
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class DebugWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ExecuteEventService executeEventService;

    @MockitoBean
    private ShopService shopService;

    @MockitoBean
    private CombatEngine combatEngine;

    @Test
    void debugPageShouldRender() throws Exception {
        mockMvc.perform(get("/debug"))
                .andExpect(status().isOk())
                .andExpect(view().name("debug"));
    }

    @Test
    void createUserShouldDelegateToUserService() throws Exception {
        mockMvc.perform(post("/api/debug/create-user")
                .param("username", "cassius")
                .param("email", "cassius@test.com")
                .param("password", "secret"))
                .andExpect(status().isOk())
                .andExpect(content().string("User created: cassius (cassius@test.com)"));

        verify(userService).register("cassius", "cassius@test.com", "secret");
    }

    @Test
    void createCharacterShouldAssignCharacterAndPersistUser() throws Exception {
        User user = new User("cassius", "cassius@test.com", "hash");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/debug/create-character")
                .param("email", user.getEmail())
                .param("name", "Cassius"))
                .andExpect(status().isOk())
                .andExpect(content().string("Character created: Cassius"));

        assertNotNull(user.getCharacter());
        assertEquals("Cassius", user.getCharacter().getName());
        verify(userRepository).save(user);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/debug/add-strength",
            "/api/debug/add-intelligence",
            "/api/debug/add-agility",
            "/api/debug/add-luck"
    })
    void addStatEndpointsShouldRejectNegativeAmounts(String endpoint) throws Exception {
        String safeEndpoint = Objects.requireNonNull(endpoint);

        mockMvc.perform(post(safeEndpoint)
                .param("email", "cassius@test.com")
                .param("amount", "-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Amount must be positive"));

        verifyNoInteractions(userRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1 })
    void gainXpEndpointShouldRejectNonPositiveAmounts(int amount) throws Exception {
        mockMvc.perform(post("/api/debug/gain-xp")
                .param("email", "cassius@test.com")
                .param("amount", Integer.toString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Amount must be positive"));

        verifyNoInteractions(userRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1 })
    void addGoldEndpointShouldRejectNonPositiveAmounts(int amount) throws Exception {
        mockMvc.perform(post("/api/debug/add-gold")
                .param("email", "cassius@test.com")
                .param("amount", Integer.toString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Amount must be positive"));

        verifyNoInteractions(userRepository);
    }

    @Test
    void trainEndpointShouldExposeValidationMessage() throws Exception {
        User user = createUserWithCharacter();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(executeEventService.executeEvent(eq(user.getId()), any(TrainingEvent.class)))
                .thenThrow(new IllegalArgumentException("Invalid stat: wisdom"));

        mockMvc.perform(post("/api/debug/train")
                .param("email", user.getEmail())
                .param("stat", "wisdom"))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid stat: wisdom"));
    }

    @Test
    void characterEndpointShouldExposeLuck() throws Exception {
        User user = createUserWithCharacter();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/debug/character")
                .param("email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(
                        result.getResponse().getContentAsString().contains("LUCK: 5")));
    }

    @Test
    void characterSheetEndpointShouldExposeStructuredCharacterData() throws Exception {
        User user = createUserWithCharacter();
        user.getCharacter().gainExperience(40);
        user.getCharacter().addGold(25);
        InventoryItem sword = new InventoryItem("Sword", EquipmentSlot.WEAPON, 40, 2, 0, 0, 0, 2, 0);
        user.getCharacter().addItem(sword);
        user.getCharacter().equipItem(sword.getId());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/debug/character-sheet")
                .param("email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cassius"))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$.experience").value(40))
                .andExpect(jsonPath("$.experienceRequiredForNextLevel").value(100))
                .andExpect(jsonPath("$.gold").value(25))
                .andExpect(jsonPath("$.strength").value(7))
                .andExpect(jsonPath("$.weaponDamageBonus").value(2))
                .andExpect(jsonPath("$.armorBonus").value(0))
                .andExpect(jsonPath("$.inventoryUsage").value(1))
                .andExpect(jsonPath("$.equipmentSlots[0].displayName").value("Weapon"))
                .andExpect(jsonPath("$.equipmentSlots[0].itemName").value("Sword"))
                .andExpect(jsonPath("$.equipmentSlots[0].rarity").value("common"))
                .andExpect(jsonPath("$.equipmentSlots[0].level").value(1))
                .andExpect(jsonPath("$.equipmentSlots[0].weaponDamage").value(2))
                .andExpect(jsonPath("$.inventoryItems[0].name").value("Sword"))
                .andExpect(jsonPath("$.inventoryItems[0].rarityDisplayName").value("Common"))
                .andExpect(jsonPath("$.inventoryItems[0].level").value(1))
                .andExpect(jsonPath("$.inventoryItems[0].weaponDamage").value(2))
                .andExpect(jsonPath("$.luck").value(5));
    }

    @Test
    void addItemPresetEndpointShouldGrantItemAndPersistUser() throws Exception {
        User user = createUserWithCharacter();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        int expectedValue = ItemValueCalculator.calculateGoldValue(
                EquipmentSlot.WEAPON,
                3,
                0,
                0,
                0,
                0,
                4,
                0);

        mockMvc.perform(post("/api/debug/add-item-preset")
                .param("email", user.getEmail())
                .param("preset", "training-sword")
                .param("level", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Granted item: Training Sword [Weapon] Common Lv.3 value " + expectedValue));

        assertEquals(1, user.getCharacter().getInventoryUsage());
        InventoryItem item = user.getCharacter().getInventoryItems().get(0);
        assertEquals(3, item.getLevel());
        assertEquals(ItemRarity.COMMON, item.getRarity());
        assertEquals(4, item.getWeaponDamage());
        verify(userRepository).save(user);
    }

    @Test
    void createItemEndpointShouldGrantCustomItemAndPersistUser() throws Exception {
        User user = createUserWithCharacter();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        int expectedValue = ItemValueCalculator.calculateGoldValue(
                EquipmentSlot.WEAPON,
                3,
                1,
                0,
                0,
                0,
                3,
                0);

        mockMvc.perform(post("/api/debug/create-item")
                .param("email", user.getEmail())
                .param("name", "Bronze Spear")
                .param("slot", "weapon")
                .param("level", "3")
                .param("weaponDamage", "3")
                .param("strengthBonus", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Created item: Bronze Spear [Weapon] Common Lv.3 value " + expectedValue));

        assertEquals(1, user.getCharacter().getInventoryUsage());
        InventoryItem item = user.getCharacter().getInventoryItems().get(0);
        assertEquals("Bronze Spear", item.getName());
        assertEquals(3, item.getLevel());
        assertEquals(ItemRarity.COMMON, item.getRarity());
        assertEquals(expectedValue, item.getGoldValue());
        assertEquals(3, item.getWeaponDamage());
        assertEquals(1, item.getStrengthBonus());
        verify(userRepository).save(user);
    }

    @Test
    void equipItemEndpointShouldEquipItem() throws Exception {
        User user = createUserWithCharacter();
        InventoryItem sword = new InventoryItem("Sword", EquipmentSlot.WEAPON, 40, 2, 0, 0, 0, 2, 0);
        user.getCharacter().addItem(sword);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/debug/equip-item")
                .param("email", user.getEmail())
                .param("itemId", sword.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Equipped item: " + sword.getId()));

        assertTrue(user.getCharacter().getEquippedItem(EquipmentSlot.WEAPON).isPresent());
        verify(userRepository).save(user);
    }

    @Test
    void shopOffersEndpointShouldExposeVendorStock() throws Exception {
        when(shopService.getCommonOffers("cassius@test.com", 2))
                .thenReturn(new ShopService.ShopCatalog(
                        3,
                        2,
                        120,
                        java.util.List.of(new ShopService.ShopOffer(
                                "training-sword",
                                "Training Sword",
                                "weapon",
                                "Weapon",
                                "common",
                                "Common",
                                2,
                                84,
                                62,
                                0,
                                0,
                                0,
                                0,
                                3,
                                0,
                                true,
                                "DMG +3"))));

        mockMvc.perform(get("/api/debug/shop-offers")
                .param("email", "cassius@test.com")
                .param("level", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.characterLevel").value(3))
                .andExpect(jsonPath("$.selectedLevel").value(2))
                .andExpect(jsonPath("$.gold").value(120))
                .andExpect(jsonPath("$.offers[0].presetId").value("training-sword"))
                .andExpect(jsonPath("$.offers[0].name").value("Training Sword"))
                .andExpect(jsonPath("$.offers[0].rarityDisplayName").value("Common"))
                .andExpect(jsonPath("$.offers[0].price").value(84))
                .andExpect(jsonPath("$.offers[0].weaponDamage").value(3))
                .andExpect(jsonPath("$.offers[0].affordable").value(true));
    }

    @Test
    void buyItemEndpointShouldPurchaseFromShopService() throws Exception {
        when(shopService.buyCommonItem("cassius@test.com", "training-sword", 2))
                .thenReturn(new ShopService.ShopPurchase(
                        "Training Sword",
                        "Weapon",
                        "common",
                        "Common",
                        2,
                        84,
                        36));

        mockMvc.perform(post("/api/debug/buy-item")
                .param("email", "cassius@test.com")
                .param("preset", "training-sword")
                .param("level", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Purchased item: Training Sword [Weapon] Common Lv.2 for 84 gold. Gold left: 36"));
    }

    @Test
    void sellItemEndpointShouldSellThroughShopService() throws Exception {
        when(shopService.sellItem("cassius@test.com", "item-1"))
                .thenReturn(new ShopService.InventoryAction(
                        "sold",
                        "Training Sword",
                        "Weapon",
                        "common",
                        "Common",
                        2,
                        62,
                        162));

        mockMvc.perform(post("/api/debug/sell-item")
                .param("email", "cassius@test.com")
                .param("itemId", "item-1"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Sold item: Training Sword [Weapon] Common Lv.2 for 62 gold. Gold now: 162"));
    }

    @Test
    void destroyItemEndpointShouldDestroyThroughShopService() throws Exception {
        when(shopService.destroyItem("cassius@test.com", "item-2"))
                .thenReturn(new ShopService.InventoryAction(
                        "destroyed",
                        "Porphyry Ring",
                        "Ring",
                        "common",
                        "Common",
                        1,
                        0,
                        50));

        mockMvc.perform(post("/api/debug/destroy-item")
                .param("email", "cassius@test.com")
                .param("itemId", "item-2"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Destroyed item: Porphyry Ring [Ring] Common Lv.1. Gold unchanged: 50"));
    }

    @Test
    void combatEndpointShouldRenderCombatReport() throws Exception {
        User attacker = createUserWithCharacter("attacker@test.com", "Cassius");
        User defender = createUserWithCharacter("defender@test.com", "Maximus");

        when(userRepository.findByEmail(attacker.getEmail())).thenReturn(Optional.of(attacker));
        when(userRepository.findByEmail(defender.getEmail())).thenReturn(Optional.of(defender));
        when(executeEventService.executeEvent(eq(attacker.getId()), any()))
                .thenReturn(createCombatEventResult());

        mockMvc.perform(post("/api/debug/combat")
                .param("attackerEmail", attacker.getEmail())
                .param("defenderEmail", defender.getEmail()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertTrue(body.contains("Cassius vs Maximus"));
                    assertTrue(body.contains("Round 1"));
                    assertTrue(body.contains("Cassius deals 4 damage to Maximus"));
                    assertTrue(body.contains("Summary"));
                });
    }

    @Test
    void combatPresetEndpointShouldRenderPresetEnemyReport() throws Exception {
        User attacker = createUserWithCharacter("attacker@test.com", "Cassius");

        when(userRepository.findByEmail(attacker.getEmail())).thenReturn(Optional.of(attacker));
        when(executeEventService.executeEvent(eq(attacker.getId()), any()))
                .thenReturn(createPresetCombatEventResult());

        mockMvc.perform(post("/api/debug/combat-preset")
                .param("attackerEmail", attacker.getEmail())
                .param("enemyType", "brute"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertTrue(body.contains("Cassius vs Brute"));
                    assertTrue(body.contains("Outcome: Brute wins by knockout"));
                    assertTrue(body.contains("Round 1"));
                });
    }

    private User createUserWithCharacter() {
        return createUserWithCharacter("cassius@test.com", "Cassius");
    }

    private User createUserWithCharacter(String email, String characterName) {
        User user = new User(characterName.toLowerCase(), email, "hash");
        user.assignCharacter(new Character(characterName));
        return user;
    }

    private CombatEventResult createCombatEventResult() {
        CombatResult result = new CombatResult(true, 1, false);
        CombatReport report = new CombatReport(
                result,
                java.util.List.of(new CombatRoundReport(1, 4, 2, 18, 16, true)),
                18,
                16,
                false);

        return new CombatEventResult(3, 100, 1, 2, 50, report);
    }

    private CombatEventResult createPresetCombatEventResult() {
        CombatResult result = new CombatResult(false, 2, false);
        CombatReport report = new CombatReport(
                result,
                java.util.List.of(
                        new CombatRoundReport(1, 3, 5, 15, 17, true),
                        new CombatRoundReport(2, 2, 15, 0, 15, true)),
                0,
                15,
                false);

        return new CombatEventResult(3, 0, 1, 1, 0, report);
    }
}
