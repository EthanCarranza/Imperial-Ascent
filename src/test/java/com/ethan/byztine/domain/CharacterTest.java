package com.ethan.byztine.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ethan.byztine.domain.event.GameEvent;
import com.ethan.byztine.domain.event.EventResult;
import com.ethan.byztine.domain.event.TrainingEvent;
import com.ethan.byztine.domain.inventory.EquipmentSlot;
import com.ethan.byztine.domain.inventory.InventoryItem;

public class CharacterTest {

    @Test
    void characterShouldStartWithLevelOneAndBaseEnergy() {
        Character character = new Character("Marcus");

        assertEquals(1, character.getLevel().getCurrentLevel());
        assertEquals(10, character.getEnergy().getCurrentEnergy());
    }

    @Test
    void gainingLevelShouldIncreaseMaxEnergyAndRefill() {
        Character character = new Character("Marcus");

        character.getEnergy().consume(5);
        character.gainExperience(100);

        assertEquals(2, character.getLevel().getCurrentLevel());
        assertEquals(11, character.getEnergy().getMaxEnergy());
        assertEquals(11, character.getEnergy().getCurrentEnergy());
    }

    @Test
    void executingEventShouldConsumeEnergy() {

        Character character = new Character("Marcus");

        GameEvent customEvent = new GameEvent() {
            @Override
            public EventResult execute(Character character) {

                int levelBefore = character.getLevel().getCurrentLevel();

                character.getEnergy().consume(3);

                int levelAfter = character.getLevel().getCurrentLevel();

                return new EventResult(
                        3,
                        0,
                        levelBefore,
                        levelAfter,
                        0);
            }
        };

        character.executeEvent(customEvent);

        assertEquals(7, character.getEnergy().getCurrentEnergy());
    }

    @Test
    void executingEventWithoutEnoughEnergyShouldFail() {

        Character character = new Character("Marcus");

        GameEvent expensiveEvent = new GameEvent() {
            @Override
            public EventResult execute(Character character) {
                character.getEnergy().consume(10);
                return null;
            }
        };

        GameEvent smallEvent = new GameEvent() {
            @Override
            public EventResult execute(Character character) {
                character.getEnergy().consume(1);
                return null;
            }
        };

        character.executeEvent(expensiveEvent);

        assertThrows(IllegalStateException.class,
                () -> character.executeEvent(smallEvent));
    }

    @Test
    void executingEventShouldReturnEventResult() {

        Character character = new Character("Marcus");
        GameEvent event = new TrainingEvent();

        EventResult result = character.executeEvent(event);

        assertEquals(1, result.getEnergySpent());
        assertEquals(20, result.getExperienceGained());
        assertEquals(1, result.getLevelBefore());
        assertEquals(1, result.getLevelAfter());
        assertEquals(10, result.getGoldGained());
    }

    @Test
    void invalidTrainingStatShouldNotMutateCharacter() {

        Character character = new Character("Marcus");
        GameEvent event = new TrainingEvent("invalid");

        assertThrows(IllegalArgumentException.class,
                () -> character.executeEvent(event));

        assertEquals(10, character.getEnergy().getCurrentEnergy());
        assertEquals(0, character.getCurrentExperience());
        assertEquals(0, character.getGold());
        assertEquals(5, character.getStats().getStrength());
        assertEquals(5, character.getStats().getIntelligence());
        assertEquals(5, character.getStats().getAgility());
        assertEquals(5, character.getStats().getLuck());
    }

    @Test
    void spendingGoldShouldReduceBalance() {

        Character character = new Character("Marcus");

        character.addGold(20);
        character.spendGold(5);

        assertEquals(15, character.getGold());
    }

    @Test
    void spendingMoreGoldThanAvailableShouldFail() {

        Character character = new Character("Marcus");

        character.addGold(5);

        assertThrows(IllegalStateException.class,
                () -> character.spendGold(10));
    }

    @Test
    void addingNonPositiveGoldShouldFail() {

        Character character = new Character("Marcus");

        assertEquals("Gold amount must be positive",
                assertThrows(IllegalArgumentException.class,
                        () -> character.addGold(0)).getMessage());
        assertEquals("Gold amount must be positive",
                assertThrows(IllegalArgumentException.class,
                        () -> character.addGold(-1)).getMessage());
        assertEquals(0, character.getGold());
    }

    @Test
    void addingItemShouldIncreaseInventoryUsage() {
        Character character = new Character("Marcus");
        InventoryItem sword = new InventoryItem("Sword", EquipmentSlot.WEAPON, 40, 2, 0, 0, 0);

        character.addItem(sword);

        assertEquals(1, character.getInventoryUsage());
        assertEquals(12, character.getInventoryCapacity());
        assertEquals("Sword", character.getInventoryItems().get(0).getName());
    }

    @Test
    void equippingItemShouldApplyBonusesAndReplaceSameSlotItem() {
        Character character = new Character("Marcus");
        InventoryItem oldSword = new InventoryItem("Old Sword", EquipmentSlot.WEAPON, 30, 1, 0, 0, 0, 1, 0);
        InventoryItem newSword = new InventoryItem("New Sword", EquipmentSlot.WEAPON, 60, 3, 0, 0, 0, 2, 0);
        InventoryItem ring = new InventoryItem("Ring", EquipmentSlot.RING, 50, 0, 2, 0, 1);

        character.addItem(oldSword);
        character.addItem(newSword);
        character.addItem(ring);

        character.equipItem(oldSword.getId());
        character.equipItem(ring.getId());
        character.equipItem(newSword.getId());

        assertEquals(8, character.getEffectiveStrength());
        assertEquals(7, character.getEffectiveIntelligence());
        assertEquals(6, character.getEffectiveLuck());
        assertEquals(2, character.getWeaponDamageFromEquipment());
        assertEquals(0, character.getArmorFromEquipment());
        assertFalse(oldSword.isEquipped());
        assertTrue(newSword.isEquipped());
        assertTrue(ring.isEquipped());
    }

    @Test
    void equippingArmorPiecesShouldIncreaseArmorMitigation() {
        Character character = new Character("Marcus");
        InventoryItem armor = new InventoryItem("Armor", EquipmentSlot.ARMOR, 70, 1, 0, 0, 1, 0, 2);
        InventoryItem helm = new InventoryItem("Helm", EquipmentSlot.HELM, 50, 1, 0, 1, 0, 0, 1);

        character.addItem(armor);
        character.addItem(helm);

        character.equipItem(armor.getId());
        character.equipItem(helm.getId());

        assertEquals(3, character.getArmorFromEquipment());
        assertEquals(7, character.getEffectiveStrength());
        assertEquals(6, character.getEffectiveAgility());
        assertEquals(6, character.getEffectiveLuck());
    }

    @Test
    void removingEquippedItemShouldFreeSlotAndRemoveBonuses() {
        Character character = new Character("Marcus");
        InventoryItem sword = new InventoryItem("Sword", EquipmentSlot.WEAPON, 40, 2, 0, 0, 0, 2, 0);

        character.addItem(sword);
        character.equipItem(sword.getId());
        InventoryItem removed = character.removeItem(sword.getId());

        assertEquals("Sword", removed.getName());
        assertEquals(0, character.getInventoryUsage());
        assertFalse(removed.isEquipped());
        assertTrue(character.getEquippedItem(EquipmentSlot.WEAPON).isEmpty());
        assertEquals(5, character.getEffectiveStrength());
        assertEquals(0, character.getWeaponDamageFromEquipment());
    }

    @Test
    void inventoryShouldRejectItemsBeyondCapacity() {
        Character character = new Character("Marcus");

        for (int index = 0; index < character.getInventoryCapacity(); index++) {
            character.addItem(new InventoryItem(
                    "Item " + index,
                    EquipmentSlot.ACCESSORY,
                    10,
                    0,
                    0,
                    1,
                    0));
        }

        assertEquals("Inventory is full",
                assertThrows(IllegalStateException.class, () -> character.addItem(
                        new InventoryItem("Overflow", EquipmentSlot.ACCESSORY, 10, 0, 0, 1, 0)))
                        .getMessage());
    }

    @Test
    void unequippingEmptySlotShouldFail() {
        Character character = new Character("Marcus");

        assertEquals("No equipped item in slot: Weapon",
                assertThrows(IllegalStateException.class, () -> character.unequipSlot(EquipmentSlot.WEAPON))
                        .getMessage());
    }

    @Test
    void removingUnknownItemShouldFail() {
        Character character = new Character("Marcus");

        assertEquals("Item not found",
                assertThrows(IllegalArgumentException.class,
                        () -> character.removeItem(java.util.UUID.randomUUID())).getMessage());
    }
}
