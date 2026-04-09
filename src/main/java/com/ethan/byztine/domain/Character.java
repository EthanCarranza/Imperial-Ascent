package com.ethan.byztine.domain;

import com.ethan.byztine.domain.event.EventResult;
import com.ethan.byztine.domain.event.GameEvent;
import com.ethan.byztine.domain.inventory.EquipmentSlot;
import com.ethan.byztine.domain.inventory.InventoryItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.ToIntFunction;

@Entity
@Table(name = "characters")
public class Character {

    private static final int BASE_ENERGY = 10;
    private static final int INVENTORY_CAPACITY = 12;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Embedded
    private Level level;

    @Embedded
    private Energy energy;

    @Embedded
    private Stats stats;

    private int gold;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "character_id")
    @OrderColumn(name = "inventory_position")
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    protected Character() {
    }

    public Character(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.name = name;
        this.level = new Level();
        this.energy = new Energy(calculateMaxEnergy());
        this.gold = 0;
        this.stats = new Stats(5, 5, 5, 5);
    }

    private int calculateMaxEnergy() {
        return BASE_ENERGY + (level.getCurrentLevel() - 1);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Level getLevel() {
        return level;
    }

    public Stats getStats() {
        return stats;
    }

    public int getCurrentLevel() {
        return level.getCurrentLevel();
    }

    public int getCurrentExperience() {
        return level.getCurrentExperience();
    }

    public Energy getEnergy() {
        return energy;
    }

    public int getGold() {
        return gold;
    }

    public List<InventoryItem> getInventoryItems() {
        return List.copyOf(inventoryItems);
    }

    public int getInventoryCapacity() {
        return INVENTORY_CAPACITY;
    }

    public int getInventoryUsage() {
        return inventoryItems.size();
    }

    public int getStrengthBonusFromEquipment() {
        return calculateEquippedBonus(InventoryItem::getStrengthBonus);
    }

    public int getIntelligenceBonusFromEquipment() {
        return calculateEquippedBonus(InventoryItem::getIntelligenceBonus);
    }

    public int getAgilityBonusFromEquipment() {
        return calculateEquippedBonus(InventoryItem::getAgilityBonus);
    }

    public int getLuckBonusFromEquipment() {
        return calculateEquippedBonus(InventoryItem::getLuckBonus);
    }

    public int getWeaponDamageFromEquipment() {
        return calculateEquippedBonus(InventoryItem::getWeaponDamage);
    }

    public int getArmorFromEquipment() {
        return calculateEquippedBonus(InventoryItem::getArmor);
    }

    public int getEffectiveStrength() {
        return stats.getStrength() + getStrengthBonusFromEquipment();
    }

    public int getEffectiveIntelligence() {
        return stats.getIntelligence() + getIntelligenceBonusFromEquipment();
    }

    public int getEffectiveAgility() {
        return stats.getAgility() + getAgilityBonusFromEquipment();
    }

    public int getEffectiveLuck() {
        return stats.getLuck() + getLuckBonusFromEquipment();
    }

    public void gainExperience(int amount) {
        boolean leveledUp = level.addExperience(amount);

        if (leveledUp) {
            energy.updateMaxEnergy(calculateMaxEnergy());
        }
    }

    public EventResult executeEvent(GameEvent event) {
        return event.execute(this);
    }

    public void addGold(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Gold amount must be positive");
        }
        this.gold += amount;
    }

    public void spendGold(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > gold) {
            throw new IllegalStateException("Not enough gold");
        }
        this.gold -= amount;
    }

    public void addItem(InventoryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        if (inventoryItems.size() >= INVENTORY_CAPACITY) {
            throw new IllegalStateException("Inventory is full");
        }

        inventoryItems.add(item);
    }

    public InventoryItem removeItem(UUID itemId) {
        InventoryItem item = requireInventoryItem(itemId);
        item.unequip();
        inventoryItems.remove(item);
        return item;
    }

    public void equipItem(UUID itemId) {
        InventoryItem item = requireInventoryItem(itemId);

        unequipEquippedItem(item.getSlot());
        item.equip();
    }

    public void unequipSlot(EquipmentSlot slot) {
        if (slot == null) {
            throw new IllegalArgumentException("Equipment slot cannot be null");
        }

        InventoryItem equippedItem = findEquippedItem(slot)
                .orElseThrow(() -> new IllegalStateException(
                        "No equipped item in slot: " + slot.getDisplayName()));

        equippedItem.unequip();
    }

    public Optional<InventoryItem> getEquippedItem(EquipmentSlot slot) {
        return findEquippedItem(slot);
    }

    public EventResult applyTraining(int energyCost, int xp, int goldReward) {

        int levelBefore = getCurrentLevel();

        energy.consume(energyCost);
        gainExperience(xp);
        addGold(goldReward);

        int levelAfter = getCurrentLevel();

        return new EventResult(
                energyCost,
                xp,
                levelBefore,
                levelAfter,
                goldReward);
    }

    private InventoryItem requireInventoryItem(UUID itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item id cannot be null");
        }

        return inventoryItems.stream()
                .filter(item -> itemId.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    private Optional<InventoryItem> findEquippedItem(EquipmentSlot slot) {
        return inventoryItems.stream()
                .filter(InventoryItem::isEquipped)
                .filter(item -> item.getSlot() == slot)
                .findFirst();
    }

    private void unequipEquippedItem(EquipmentSlot slot) {
        findEquippedItem(slot).ifPresent(InventoryItem::unequip);
    }

    private int calculateEquippedBonus(ToIntFunction<InventoryItem> extractor) {
        return inventoryItems.stream()
                .filter(InventoryItem::isEquipped)
                .mapToInt(extractor)
                .sum();
    }
}
