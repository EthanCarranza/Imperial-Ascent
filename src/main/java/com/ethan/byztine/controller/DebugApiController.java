package com.ethan.byztine.controller;

import com.ethan.byztine.application.ExecuteEventService;
import com.ethan.byztine.application.ShopService;
import com.ethan.byztine.application.UserService;
import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.combat.CombatReport;
import com.ethan.byztine.domain.combat.CombatRoundReport;
import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.combat.EnemyFactory;
import com.ethan.byztine.domain.combat.EnemyPreset;
import com.ethan.byztine.domain.event.CombatEvent;
import com.ethan.byztine.domain.event.CombatEventResult;
import com.ethan.byztine.domain.event.EventResult;
import com.ethan.byztine.domain.event.TrainingEvent;
import com.ethan.byztine.domain.exploration.ExplorationEncounter;
import com.ethan.byztine.domain.exploration.ExplorationEvent;
import com.ethan.byztine.domain.exploration.ExplorationEventResult;
import com.ethan.byztine.domain.exploration.ExplorationLootEntry;
import com.ethan.byztine.domain.exploration.ExplorationZone;
import com.ethan.byztine.domain.inventory.EquipmentSlot;
import com.ethan.byztine.domain.inventory.InventoryItem;
import com.ethan.byztine.domain.inventory.ItemPreset;
import com.ethan.byztine.domain.inventory.ItemRarity;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/debug")
public class DebugApiController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ExecuteEventService executeEventService;
    private final ShopService shopService;
    private final CombatEngine combatEngine;

    public DebugApiController(
            UserRepository userRepository,
            UserService userService,
            ExecuteEventService executeEventService,
            ShopService shopService,
            CombatEngine combatEngine) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.executeEventService = executeEventService;
        this.shopService = shopService;
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

    @PostMapping("/add-item-preset")
    public String addItemPreset(@RequestParam String email,
            @RequestParam String preset,
            @RequestParam(defaultValue = "1") int level,
            @RequestParam(defaultValue = "common") String rarity) {

        return handleRequest(() -> {
            User user = requireUserByEmail(email, "User");
            Character character = requireCharacter(user, "User");
            ItemPreset itemPreset = ItemPreset.fromId(preset);
            ItemRarity itemRarity = ItemRarity.fromId(rarity);
            requireMinimumOne(level, "Item level");
            InventoryItem item = InventoryItem.fromPreset(itemPreset, level, itemRarity);

            character.addItem(item);
            userRepository.save(user);

            return "Granted item: " + item.getName() + " ["
                    + item.getSlot().getDisplayName() + "] "
                    + item.getRarity().getDisplayName() + " Lv." + item.getLevel()
                    + " value " + item.getGoldValue();
        });
    }

    @PostMapping("/create-item")
    public String createItem(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String slot,
            @RequestParam(defaultValue = "1") int level,
            @RequestParam(defaultValue = "common") String rarity,
            @RequestParam(defaultValue = "0") int strengthBonus,
            @RequestParam(defaultValue = "0") int intelligenceBonus,
            @RequestParam(defaultValue = "0") int agilityBonus,
            @RequestParam(defaultValue = "0") int luckBonus,
            @RequestParam(defaultValue = "0") int weaponDamage,
            @RequestParam(defaultValue = "0") int armor) {

        return handleRequest(() -> {
            User user = requireUserByEmail(email, "User");
            Character character = requireCharacter(user, "User");
            EquipmentSlot equipmentSlot = EquipmentSlot.fromId(slot);
            ItemRarity itemRarity = ItemRarity.fromId(rarity);

            requireMinimumOne(level, "Item level");
            requireNonNegativeAmount(strengthBonus, "Strength bonus");
            requireNonNegativeAmount(intelligenceBonus, "Intelligence bonus");
            requireNonNegativeAmount(agilityBonus, "Agility bonus");
            requireNonNegativeAmount(luckBonus, "Luck bonus");
            requireNonNegativeAmount(weaponDamage, "Weapon damage");
            requireNonNegativeAmount(armor, "Armor");

            InventoryItem item = InventoryItem.scaled(
                    name,
                    equipmentSlot,
                    itemRarity,
                    level,
                    strengthBonus,
                    intelligenceBonus,
                    agilityBonus,
                    luckBonus,
                    weaponDamage,
                    armor);

            character.addItem(item);
            userRepository.save(user);

            return "Created item: " + item.getName() + " ["
                    + item.getSlot().getDisplayName() + "] "
                    + item.getRarity().getDisplayName() + " Lv." + item.getLevel()
                    + " value " + item.getGoldValue();
        });
    }

    @PostMapping("/equip-item")
    public String equipItem(@RequestParam String email,
            @RequestParam String itemId) {

        return handleRequest(() -> {
            User user = requireUserByEmail(email, "User");
            Character character = requireCharacter(user, "User");
            UUID parsedItemId = parseUuid(itemId, "Item id");

            character.equipItem(parsedItemId);
            userRepository.save(user);

            return "Equipped item: " + parsedItemId;
        });
    }

    @PostMapping("/unequip-slot")
    public String unequipSlot(@RequestParam String email,
            @RequestParam String slot) {

        return handleRequest(() -> {
            User user = requireUserByEmail(email, "User");
            Character character = requireCharacter(user, "User");
            EquipmentSlot equipmentSlot = EquipmentSlot.fromId(slot);

            character.unequipSlot(equipmentSlot);
            userRepository.save(user);

            return "Unequipped slot: " + equipmentSlot.getDisplayName();
        });
    }

    @GetMapping("/shop-offers")
    public ResponseEntity<?> getShopOffers(
            @RequestParam String email,
            @RequestParam(required = false) Integer level) {

        try {
            ShopService.ShopCatalog catalog = shopService.getCommonOffers(email, level);

            return ResponseEntity.ok(new ShopCatalogResponse(
                    catalog.characterLevel(),
                    catalog.selectedLevel(),
                    catalog.gold(),
                    catalog.offers().stream()
                            .map(offer -> new ShopOfferResponse(
                                    offer.presetId(),
                                    offer.name(),
                                    offer.slot(),
                                    offer.slotDisplayName(),
                                    offer.rarity(),
                                    offer.rarityDisplayName(),
                                    offer.level(),
                                    offer.price(),
                                    offer.goldValue(),
                                    offer.strengthBonus(),
                                    offer.intelligenceBonus(),
                                    offer.agilityBonus(),
                                    offer.luckBonus(),
                                    offer.weaponDamage(),
                                    offer.armor(),
                                    offer.affordable(),
                                    offer.bonusSummary()))
                            .toList()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/buy-item")
    public String buyItem(
            @RequestParam String email,
            @RequestParam String preset,
            @RequestParam int level) {

        return handleRequest(() -> {
            ShopService.ShopPurchase purchase = shopService.buyCommonItem(email, preset, level);

            return "Purchased item: " + purchase.itemName() + " ["
                    + purchase.slotDisplayName() + "] "
                    + purchase.rarityDisplayName() + " Lv." + purchase.itemLevel()
                    + " for " + purchase.price() + " gold. Gold left: "
                    + purchase.goldRemaining();
        });
    }

    @PostMapping("/sell-item")
    public String sellItem(
            @RequestParam String email,
            @RequestParam String itemId) {

        return handleRequest(() -> {
            ShopService.InventoryAction action = shopService.sellItem(email, itemId);

            return "Sold item: " + action.itemName() + " ["
                    + action.slotDisplayName() + "] "
                    + action.rarityDisplayName() + " Lv." + action.itemLevel()
                    + " for " + action.goldDelta() + " gold. Gold now: "
                    + action.goldRemaining();
        });
    }

    @PostMapping("/destroy-item")
    public String destroyItem(
            @RequestParam String email,
            @RequestParam String itemId) {

        return handleRequest(() -> {
            ShopService.InventoryAction action = shopService.destroyItem(email, itemId);

            return "Destroyed item: " + action.itemName() + " ["
                    + action.slotDisplayName() + "] "
                    + action.rarityDisplayName() + " Lv." + action.itemLevel()
                    + ". Gold unchanged: " + action.goldRemaining();
        });
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

            return formatCombatResponse(attacker, defender, result);
        });
    }

    @PostMapping("/combat-preset")
    public String combatPreset(
            @RequestParam String attackerEmail,
            @RequestParam String enemyType) {

        return handleRequest(() -> {
            User attackerUser = requireUserByEmail(attackerEmail, "Attacker");
            Character attacker = requireCharacter(attackerUser, "Attacker");
            Character enemy = EnemyFactory.create(EnemyPreset.fromId(enemyType));

            EventResult result = executeEventService.executeEvent(
                    attackerUser.getId(),
                    new CombatEvent(combatEngine, enemy));

            return formatCombatResponse(attacker, enemy, result);
        });
    }

    @GetMapping("/exploration-zones")
    public ResponseEntity<?> getExplorationZones() {
        return ResponseEntity.ok(new ExplorationCatalogResponse(
                Arrays.stream(ExplorationZone.values())
                        .map(this::buildExplorationZone)
                        .toList()));
    }

    @PostMapping("/explore")
    public String explore(
            @RequestParam String email,
            @RequestParam String zone,
            @RequestParam String encounter) {

        return handleRequest(() -> {
            User user = requireUserByEmail(email, "User");
            Character attacker = requireCharacter(user, "User");
            ExplorationZone explorationZone = ExplorationZone.fromId(zone);
            ExplorationEncounter selectedEncounter = ExplorationEncounter.fromZoneAndId(
                    explorationZone,
                    encounter);

            EventResult result = executeEventService.executeEvent(
                    user.getId(),
                    new ExplorationEvent(combatEngine, selectedEncounter));

            return formatExplorationResponse(attacker, selectedEncounter, result);
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
                    "\nWeapon Damage: " + character.getWeaponDamageFromEquipment() +
                    "\nArmor: " + character.getArmorFromEquipment() +
                    "\nSTR: " + character.getEffectiveStrength() +
                    " (base " + character.getStats().getStrength() + ")" +
                    "\nINT: " + character.getEffectiveIntelligence() +
                    " (base " + character.getStats().getIntelligence() + ")" +
                    "\nAGI: " + character.getEffectiveAgility() +
                    " (base " + character.getStats().getAgility() + ")" +
                    "\nLUCK: " + character.getEffectiveLuck() +
                    " (base " + character.getStats().getLuck() + ")" +
                    "\nInventory: " + character.getInventoryUsage() + " / "
                    + character.getInventoryCapacity();
        });
    }

    @GetMapping("/character-sheet")
    public ResponseEntity<?> getCharacterSheet(@RequestParam String email) {

        try {
            User user = requireUserByEmail(email, "User");
            Character character = requireCharacter(user, "User");

            return ResponseEntity.ok(new CharacterSheetResponse(
                    user.getUsername(),
                    user.getEmail(),
                    character.getName(),
                    character.getCurrentLevel(),
                    character.getCurrentExperience(),
                    character.getCurrentLevel() * 100,
                    character.getEnergy().getCurrentEnergy(),
                    character.getEnergy().getMaxEnergy(),
                    character.getGold(),
                    character.getEffectiveStrength(),
                    character.getEffectiveIntelligence(),
                    character.getEffectiveAgility(),
                    character.getEffectiveLuck(),
                    character.getStats().getStrength(),
                    character.getStats().getIntelligence(),
                    character.getStats().getAgility(),
                    character.getStats().getLuck(),
                    character.getStrengthBonusFromEquipment(),
                    character.getIntelligenceBonusFromEquipment(),
                    character.getAgilityBonusFromEquipment(),
                    character.getLuckBonusFromEquipment(),
                    character.getWeaponDamageFromEquipment(),
                    character.getArmorFromEquipment(),
                    character.getInventoryCapacity(),
                    character.getInventoryUsage(),
                    buildEquipmentSlots(character),
                    buildInventoryItems(character)));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
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

    private void requireMinimumOne(int amount, String label) {
        if (amount <= 0) {
            throw new IllegalArgumentException(label + " must be positive");
        }
    }

    private void requireNonNegativeAmount(int amount, String label) {
        if (amount < 0) {
            throw new IllegalArgumentException(label + " cannot be negative");
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

    private UUID parseUuid(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is invalid");
        }

        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(label + " is invalid");
        }
    }

    private List<EquipmentSlotResponse> buildEquipmentSlots(Character character) {
        return Arrays.stream(EquipmentSlot.values())
                .map(slot -> {
                    InventoryItem item = character.getEquippedItem(slot).orElse(null);

                    if (item == null) {
                        return new EquipmentSlotResponse(
                                slot.getId(),
                                slot.getDisplayName(),
                                null,
                                null,
                                null,
                                null,
                                0,
                                0,
                                0,
                                0,
                                0,
                                0,
                                0,
                                "Empty");
                    }

                    return new EquipmentSlotResponse(
                            slot.getId(),
                            slot.getDisplayName(),
                            item.getId() == null ? null : item.getId().toString(),
                            item.getName(),
                            item.getRarity().getId(),
                            item.getRarity().getDisplayName(),
                            item.getLevel(),
                            item.getStrengthBonus(),
                            item.getIntelligenceBonus(),
                            item.getAgilityBonus(),
                            item.getLuckBonus(),
                            item.getWeaponDamage(),
                            item.getArmor(),
                            item.getBonusSummary());
                })
                .toList();
    }

    private List<InventoryItemResponse> buildInventoryItems(Character character) {
        return character.getInventoryItems().stream()
                .map(item -> new InventoryItemResponse(
                        item.getId() == null ? null : item.getId().toString(),
                        item.getName(),
                        item.getSlot().getId(),
                        item.getSlot().getDisplayName(),
                        item.getRarity().getId(),
                        item.getRarity().getDisplayName(),
                        item.getLevel(),
                        item.isEquipped(),
                        item.getGoldValue(),
                        item.getStrengthBonus(),
                        item.getIntelligenceBonus(),
                        item.getAgilityBonus(),
                        item.getLuckBonus(),
                        item.getWeaponDamage(),
                        item.getArmor(),
                        item.getBonusSummary()))
                .toList();
    }

    private String formatCombatResponse(
            Character attacker,
            Character defender,
            EventResult result) {

        if (!(result instanceof CombatEventResult combatEventResult)) {
            return "Combat\n\n" +
                    attacker.getName() + " vs " + defender.getName() + "\n\n" +
                    result;
        }

        CombatReport report = combatEventResult.getCombatReport();
        StringBuilder builder = new StringBuilder();

        builder.append("Combat\n\n")
                .append(attacker.getName())
                .append(" vs ")
                .append(defender.getName())
                .append("\n")
                .append("Outcome: ")
                .append(formatCombatOutcome(attacker.getName(), defender.getName(), report))
                .append("\n")
                .append("Rounds: ")
                .append(report.getResult().getRounds())
                .append("\n")
                .append("Final HP: ")
                .append(attacker.getName())
                .append(" ")
                .append(report.getAttackerFinalHealth())
                .append(" | ")
                .append(defender.getName())
                .append(" ")
                .append(report.getDefenderFinalHealth());

        for (CombatRoundReport round : report.getRounds()) {
            builder.append("\n\n")
                    .append(formatRound(attacker.getName(), defender.getName(), round));
        }

        builder.append("\n\nSummary\n")
                .append(result);

        return builder.toString();
    }

    private String formatExplorationResponse(
            Character attacker,
            ExplorationEncounter encounter,
            EventResult result) {

        if (!(result instanceof ExplorationEventResult explorationResult)) {
            return "Exploration\n\n"
                    + "Zone: "
                    + encounter.getZone().getDisplayName()
                    + "\nEncounter: "
                    + encounter.getDisplayName()
                    + "\n\n"
                    + result;
        }

        CombatReport report = explorationResult.getCombatReport();
        StringBuilder builder = new StringBuilder();

        builder.append("Exploration\n\n")
                .append("Zone: ")
                .append(explorationResult.getZoneDisplayName())
                .append("\nEncounter: ")
                .append(explorationResult.getEncounterDisplayName())
                .append(" | Lv. ")
                .append(explorationResult.getEncounterLevel());

        if (explorationResult.isBossEncounter()) {
            builder.append(" | Boss");
        }

        builder.append("\n")
                .append(attacker.getName())
                .append(" vs ")
                .append(explorationResult.getEncounterDisplayName())
                .append("\n")
                .append("Outcome: ")
                .append(formatCombatOutcome(
                        attacker.getName(),
                        explorationResult.getEncounterDisplayName(),
                        report))
                .append("\n")
                .append("Rounds: ")
                .append(report.getResult().getRounds())
                .append("\n")
                .append("Final HP: ")
                .append(attacker.getName())
                .append(" ")
                .append(report.getAttackerFinalHealth())
                .append(" | ")
                .append(explorationResult.getEncounterDisplayName())
                .append(" ")
                .append(report.getDefenderFinalHealth());

        for (CombatRoundReport round : report.getRounds()) {
            builder.append("\n\n")
                    .append(formatRound(attacker.getName(), explorationResult.getEncounterDisplayName(), round));
        }

        builder.append("\n\nSummary\n")
                .append(result);

        return builder.toString();
    }

    private String formatCombatOutcome(
            String attackerName,
            String defenderName,
            CombatReport report) {

        if (report.getResult().isDraw()) {
            return report.reachedRoundLimit()
                    ? "Draw after 20 rounds"
                    : "Draw";
        }

        String winner = report.getResult().attackerWon()
                ? attackerName
                : defenderName;

        if (report.reachedRoundLimit()) {
            return winner + " wins on health after 20 rounds";
        }

        return winner + " wins by knockout";
    }

    private ExplorationZoneResponse buildExplorationZone(ExplorationZone zone) {
        return new ExplorationZoneResponse(
                zone.getId(),
                zone.getDisplayName(),
                zone.getRecommendedLevelMin(),
                zone.getRecommendedLevelMax(),
                zone.getDescription(),
                zone.getEncounters().stream()
                        .map(this::buildExplorationEncounter)
                        .toList());
    }

    private ExplorationEncounterResponse buildExplorationEncounter(ExplorationEncounter encounter) {
        return new ExplorationEncounterResponse(
                encounter.getId(),
                encounter.getDisplayName(),
                encounter.getLevel(),
                encounter.isBoss(),
                encounter.getStrength(),
                encounter.getIntelligence(),
                encounter.getAgility(),
                encounter.getLuck(),
                encounter.getEnergyCost(),
                encounter.getWinExperience(),
                encounter.getLossExperience(),
                encounter.getGoldReward(),
                encounter.getDropChance(),
                buildExplorationDropPreview(encounter));
    }

    private List<String> buildExplorationDropPreview(ExplorationEncounter encounter) {
        Map<String, List<String>> rarityMap = new LinkedHashMap<>();
        Map<String, String> labels = new LinkedHashMap<>();

        for (ExplorationLootEntry entry : encounter.getLootTable()) {
            String key = entry.preset().getId() + ":" + entry.level();
            labels.putIfAbsent(
                    key,
                    entry.preset().getDisplayName()
                            + " ["
                            + entry.preset().getSlot().getDisplayName()
                            + "] Lv."
                            + entry.level());
            rarityMap.computeIfAbsent(key, ignored -> new ArrayList<>());

            if (!rarityMap.get(key).contains(entry.rarity().getDisplayName())) {
                rarityMap.get(key).add(entry.rarity().getDisplayName());
            }
        }

        return rarityMap.entrySet().stream()
                .map(entry -> labels.get(entry.getKey()) + " - " + String.join("/", entry.getValue()))
                .toList();
    }

    private String formatRound(
            String attackerName,
            String defenderName,
            CombatRoundReport round) {

        StringBuilder builder = new StringBuilder();
        builder.append("Round ")
                .append(round.roundNumber())
                .append("\n- ")
                .append(formatAttackLine(
                        attackerName,
                        defenderName,
                        round.attackerDamage(),
                        round.defenderHealthAfterRound()));

        if (!round.defenderActed()) {
            builder.append("\n- ")
                    .append(defenderName)
                    .append(" is defeated before acting.");
        } else {
            builder.append("\n- ")
                    .append(formatAttackLine(
                            defenderName,
                            attackerName,
                            round.defenderDamage(),
                            round.attackerHealthAfterRound()));
        }

        builder.append("\n- End of round: ")
                .append(attackerName)
                .append(" ")
                .append(round.attackerHealthAfterRound())
                .append(" HP | ")
                .append(defenderName)
                .append(" ")
                .append(round.defenderHealthAfterRound())
                .append(" HP");

        return builder.toString();
    }

    private String formatAttackLine(
            String actorName,
            String targetName,
            int damage,
            int targetHealthAfterAttack) {

        if (damage == 0) {
            return actorName + " misses. " + targetName + " stays at " + targetHealthAfterAttack + " HP.";
        }

        return actorName + " deals " + damage + " damage to " + targetName
                + ". " + targetName + " is now at " + targetHealthAfterAttack + " HP.";
    }

    private record CharacterSheetResponse(
            String username,
            String email,
            String name,
            int level,
            int experience,
            int experienceRequiredForNextLevel,
            int currentEnergy,
            int maxEnergy,
            int gold,
            int strength,
            int intelligence,
            int agility,
            int luck,
            int baseStrength,
            int baseIntelligence,
            int baseAgility,
            int baseLuck,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamageBonus,
            int armorBonus,
            int inventoryCapacity,
            int inventoryUsage,
            List<EquipmentSlotResponse> equipmentSlots,
            List<InventoryItemResponse> inventoryItems) {
    }

    private record EquipmentSlotResponse(
            String slot,
            String displayName,
            String itemId,
            String itemName,
            String rarity,
            String rarityDisplayName,
            int level,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor,
            String bonusSummary) {
    }

    private record InventoryItemResponse(
            String id,
            String name,
            String slot,
            String slotDisplayName,
            String rarity,
            String rarityDisplayName,
            int level,
            boolean equipped,
            int goldValue,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor,
            String bonusSummary) {
    }

    private record ShopCatalogResponse(
            int characterLevel,
            int selectedLevel,
            int gold,
            List<ShopOfferResponse> offers) {
    }

    private record ShopOfferResponse(
            String presetId,
            String name,
            String slot,
            String slotDisplayName,
            String rarity,
            String rarityDisplayName,
            int level,
            int price,
            int goldValue,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor,
            boolean affordable,
            String bonusSummary) {
    }

    private record ExplorationCatalogResponse(
            List<ExplorationZoneResponse> zones) {
    }

    private record ExplorationZoneResponse(
            String id,
            String displayName,
            int recommendedLevelMin,
            int recommendedLevelMax,
            String description,
            List<ExplorationEncounterResponse> encounters) {
    }

    private record ExplorationEncounterResponse(
            String id,
            String displayName,
            int level,
            boolean boss,
            int strength,
            int intelligence,
            int agility,
            int luck,
            int energyCost,
            int winExperience,
            int lossExperience,
            int goldReward,
            int dropChance,
            List<String> possibleDrops) {
    }
}
