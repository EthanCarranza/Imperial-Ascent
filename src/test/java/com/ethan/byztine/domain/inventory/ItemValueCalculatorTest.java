package com.ethan.byztine.domain.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemValueCalculatorTest {

    @Test
    void higherLevelShouldIncreaseCalculatedGoldValue() {
        int levelOneValue = ItemValueCalculator.calculateGoldValue(
                EquipmentSlot.WEAPON,
                1,
                0,
                0,
                0,
                0,
                2,
                0);
        int levelFiveValue = ItemValueCalculator.calculateGoldValue(
                EquipmentSlot.WEAPON,
                5,
                0,
                0,
                0,
                0,
                6,
                0);

        assertTrue(levelFiveValue > levelOneValue);
    }

    @Test
    void presetScalingShouldIncreaseItemLevelCombatPowerAndValue() {
        InventoryItem levelOneSword = InventoryItem.fromPreset(ItemPreset.TRAINING_SWORD, 1);
        InventoryItem levelFiveSword = InventoryItem.fromPreset(ItemPreset.TRAINING_SWORD, 5);

        assertEquals(1, levelOneSword.getLevel());
        assertEquals(5, levelFiveSword.getLevel());
        assertEquals(2, levelOneSword.getWeaponDamage());
        assertEquals(6, levelFiveSword.getWeaponDamage());
        assertTrue(levelFiveSword.getGoldValue() > levelOneSword.getGoldValue());
    }

    @Test
    void higherRarityShouldIncreaseScaledPowerAndValue() {
        InventoryItem commonSword = InventoryItem.fromPreset(ItemPreset.TRAINING_SWORD, 3, ItemRarity.COMMON);
        InventoryItem rareSword = InventoryItem.fromPreset(ItemPreset.TRAINING_SWORD, 3, ItemRarity.RARE);
        InventoryItem epicSword = InventoryItem.fromPreset(ItemPreset.TRAINING_SWORD, 3, ItemRarity.EPIC);

        assertEquals(ItemRarity.COMMON, commonSword.getRarity());
        assertEquals(ItemRarity.RARE, rareSword.getRarity());
        assertEquals(ItemRarity.EPIC, epicSword.getRarity());
        assertTrue(rareSword.getWeaponDamage() > commonSword.getWeaponDamage());
        assertTrue(epicSword.getWeaponDamage() > rareSword.getWeaponDamage());
        assertTrue(rareSword.getGoldValue() > commonSword.getGoldValue());
        assertTrue(epicSword.getGoldValue() > rareSword.getGoldValue());
    }
}
