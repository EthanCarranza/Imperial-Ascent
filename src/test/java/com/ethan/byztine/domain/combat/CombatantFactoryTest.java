package com.ethan.byztine.domain.combat;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.inventory.EquipmentSlot;
import com.ethan.byztine.domain.inventory.InventoryItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatantFactoryTest {

    @Test
    void combatantFactoryShouldMapTrainableStatsIntoCombatStats() {
        Character base = new Character("Base");
        Character trained = new Character("Trained");

        trained.getStats().increaseStrength(2);
        trained.getStats().increaseAgility(3);
        trained.getStats().increaseLuck(4);

        Combatant baseCombatant = CombatantFactory.fromCharacter(base);
        Combatant trainedCombatant = CombatantFactory.fromCharacter(trained);

        assertEquals(baseCombatant.getAttack(), trainedCombatant.getAttack());
        assertEquals(baseCombatant.getDefense(), trainedCombatant.getDefense());
        assertEquals(baseCombatant.getStrength() + 2, trainedCombatant.getStrength());
        assertEquals(baseCombatant.getAgility() + 3, trainedCombatant.getAgility());
        assertEquals(baseCombatant.getLuck() + 4, trainedCombatant.getLuck());
    }

    @Test
    void combatantFactoryShouldIncludeEquippedItemBonuses() {
        Character equipped = new Character("Equipped");
        InventoryItem sword = new InventoryItem("Sword", EquipmentSlot.WEAPON, 40, 2, 0, 0, 0, 2, 0);
        InventoryItem armor = new InventoryItem("Armor", EquipmentSlot.ARMOR, 70, 1, 0, 0, 1, 0, 2);
        InventoryItem charm = new InventoryItem("Charm", EquipmentSlot.ACCESSORY, 45, 0, 0, 1, 2);

        equipped.addItem(sword);
        equipped.addItem(armor);
        equipped.addItem(charm);
        equipped.equipItem(sword.getId());
        equipped.equipItem(armor.getId());
        equipped.equipItem(charm.getId());

        Combatant combatant = CombatantFactory.fromCharacter(equipped);

        assertEquals(6, combatant.getAttack());
        assertEquals(4, combatant.getDefense());
        assertEquals(8, combatant.getStrength());
        assertEquals(6, combatant.getAgility());
        assertEquals(8, combatant.getLuck());
        assertEquals(2, combatant.getWeaponDamage());
        assertEquals(2, combatant.getArmor());
    }
}
