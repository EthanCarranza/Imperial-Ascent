package com.ethan.byztine.application;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.inventory.InventoryItem;
import com.ethan.byztine.domain.inventory.ItemPreset;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ShopService {

    private static final double COMMON_SHOP_MARKUP = 1.35;

    private final UserRepository userRepository;

    public ShopService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ShopCatalog getCommonOffers(String email, Integer requestedLevel) {
        User user = requireUserByEmail(email);
        Character character = requireCharacter(user);
        int selectedLevel = resolveRequestedLevel(character, requestedLevel, "Shop level");

        List<ShopOffer> offers = Arrays.stream(ItemPreset.values())
                .map(preset -> buildOffer(preset, selectedLevel, character.getGold()))
                .toList();

        return new ShopCatalog(
                character.getCurrentLevel(),
                selectedLevel,
                character.getGold(),
                offers);
    }

    public ShopPurchase buyCommonItem(String email, String presetId, int requestedLevel) {
        User user = requireUserByEmail(email);
        Character character = requireCharacter(user);
        int itemLevel = resolveRequestedLevel(character, requestedLevel, "Item level");

        if (character.getInventoryUsage() >= character.getInventoryCapacity()) {
            throw new IllegalStateException("Inventory is full");
        }

        ItemPreset preset = ItemPreset.fromId(presetId);
        InventoryItem item = InventoryItem.fromPreset(preset, itemLevel);
        int price = calculateBuyPrice(item);

        if (price > character.getGold()) {
            throw new IllegalStateException("Not enough gold");
        }

        character.spendGold(price);
        character.addItem(item);
        userRepository.save(user);

        return new ShopPurchase(
                item.getName(),
                item.getSlot().getDisplayName(),
                item.getLevel(),
                price,
                character.getGold());
    }

    private ShopOffer buildOffer(ItemPreset preset, int level, int availableGold) {
        InventoryItem item = InventoryItem.fromPreset(preset, level);
        int price = calculateBuyPrice(item);

        return new ShopOffer(
                preset.getId(),
                item.getName(),
                item.getSlot().getId(),
                item.getSlot().getDisplayName(),
                item.getLevel(),
                price,
                item.getGoldValue(),
                item.getStrengthBonus(),
                item.getIntelligenceBonus(),
                item.getAgilityBonus(),
                item.getLuckBonus(),
                item.getWeaponDamage(),
                item.getArmor(),
                availableGold >= price,
                item.getBonusSummary());
    }

    private int calculateBuyPrice(InventoryItem item) {
        return (int) Math.ceil(item.getGoldValue() * COMMON_SHOP_MARKUP);
    }

    private int resolveRequestedLevel(Character character, Integer requestedLevel, String label) {
        if (requestedLevel == null) {
            return character.getCurrentLevel();
        }

        if (requestedLevel < 1) {
            throw new IllegalArgumentException(label + " must be positive");
        }

        if (requestedLevel > character.getCurrentLevel()) {
            throw new IllegalArgumentException(label + " cannot exceed character level");
        }

        return requestedLevel;
    }

    private User requireUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Character requireCharacter(User user) {
        Character character = user.getCharacter();

        if (character == null) {
            throw new IllegalStateException("User has no character");
        }

        return character;
    }

    public record ShopCatalog(
            int characterLevel,
            int selectedLevel,
            int gold,
            List<ShopOffer> offers) {
    }

    public record ShopOffer(
            String presetId,
            String name,
            String slot,
            String slotDisplayName,
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

    public record ShopPurchase(
            String itemName,
            String slotDisplayName,
            int itemLevel,
            int price,
            int goldRemaining) {
    }
}
