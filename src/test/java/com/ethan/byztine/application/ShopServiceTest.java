package com.ethan.byztine.application;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.inventory.InventoryItem;
import com.ethan.byztine.domain.inventory.ItemPreset;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.infrastructure.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopServiceTest {

    @Test
    void shouldListCommonOffersForSelectedAllowedLevel() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        ShopService service = new ShopService(repository);
        User user = createUserWithCharacter("merchant@test.com", "Merchant");
        levelUpTo(user.getCharacter(), 3);
        user.getCharacter().addGold(500);
        repository.save(user);

        ShopService.ShopCatalog catalog = service.getCommonOffers(user.getEmail(), 2);

        assertEquals(3, catalog.characterLevel());
        assertEquals(2, catalog.selectedLevel());
        assertEquals(500, catalog.gold());
        assertEquals(ItemPreset.values().length, catalog.offers().size());
        assertTrue(catalog.offers().stream().allMatch(offer -> offer.level() == 2));
        assertTrue(catalog.offers().stream().allMatch(ShopService.ShopOffer::affordable));
    }

    @Test
    void shouldDefaultShopOffersToCharacterLevel() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        ShopService service = new ShopService(repository);
        User user = createUserWithCharacter("default-shop@test.com", "Cassius");
        levelUpTo(user.getCharacter(), 4);
        repository.save(user);

        ShopService.ShopCatalog catalog = service.getCommonOffers(user.getEmail(), null);

        assertEquals(4, catalog.selectedLevel());
        assertTrue(catalog.offers().stream().allMatch(offer -> offer.level() == 4));
        assertFalse(catalog.offers().stream().allMatch(ShopService.ShopOffer::affordable));
    }

    @Test
    void shouldRejectShopOffersAboveCharacterLevel() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        ShopService service = new ShopService(repository);
        User user = createUserWithCharacter("shop-cap@test.com", "Cassius");
        levelUpTo(user.getCharacter(), 2);
        repository.save(user);

        assertEquals(
                "Shop level cannot exceed character level",
                assertThrows(IllegalArgumentException.class,
                        () -> service.getCommonOffers(user.getEmail(), 3)).getMessage());
    }

    @Test
    void shouldBuyCommonItemAndDeductGold() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        ShopService service = new ShopService(repository);
        User user = createUserWithCharacter("buyer@test.com", "Cassius");
        levelUpTo(user.getCharacter(), 3);
        user.getCharacter().addGold(500);
        repository.save(user);

        ShopService.ShopPurchase purchase = service.buyCommonItem(user.getEmail(), "training-sword", 3);

        InventoryItem purchasedItem = user.getCharacter().getInventoryItems().get(0);
        int expectedPrice = (int) Math.ceil(purchasedItem.getGoldValue() * 1.35);

        assertEquals("Training Sword", purchase.itemName());
        assertEquals("Weapon", purchase.slotDisplayName());
        assertEquals(3, purchase.itemLevel());
        assertEquals(expectedPrice, purchase.price());
        assertEquals(500 - expectedPrice, purchase.goldRemaining());
        assertEquals(1, user.getCharacter().getInventoryUsage());
        assertEquals(3, purchasedItem.getLevel());
    }

    @Test
    void shouldRejectPurchasesAboveCharacterLevel() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        ShopService service = new ShopService(repository);
        User user = createUserWithCharacter("overbuy@test.com", "Cassius");
        levelUpTo(user.getCharacter(), 2);
        user.getCharacter().addGold(500);
        repository.save(user);

        assertEquals(
                "Item level cannot exceed character level",
                assertThrows(IllegalArgumentException.class,
                        () -> service.buyCommonItem(user.getEmail(), "training-sword", 3)).getMessage());

        assertEquals(500, user.getCharacter().getGold());
        assertEquals(0, user.getCharacter().getInventoryUsage());
    }

    @Test
    void shouldRejectPurchaseWhenNotEnoughGold() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        ShopService service = new ShopService(repository);
        User user = createUserWithCharacter("poor@test.com", "Cassius");
        levelUpTo(user.getCharacter(), 2);
        user.getCharacter().addGold(10);
        repository.save(user);

        assertEquals(
                "Not enough gold",
                assertThrows(IllegalStateException.class,
                        () -> service.buyCommonItem(user.getEmail(), "training-sword", 2)).getMessage());

        assertEquals(10, user.getCharacter().getGold());
        assertEquals(0, user.getCharacter().getInventoryUsage());
    }

    @Test
    void shouldRejectPurchaseWhenInventoryIsFullBeforeSpendingGold() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        ShopService service = new ShopService(repository);
        User user = createUserWithCharacter("full@test.com", "Cassius");
        levelUpTo(user.getCharacter(), 2);
        user.getCharacter().addGold(500);

        for (int index = 0; index < user.getCharacter().getInventoryCapacity(); index++) {
            user.getCharacter().addItem(InventoryItem.fromPreset(ItemPreset.CAVALRY_CLASP, 1));
        }

        repository.save(user);

        assertEquals(
                "Inventory is full",
                assertThrows(IllegalStateException.class,
                        () -> service.buyCommonItem(user.getEmail(), "training-sword", 2)).getMessage());

        assertEquals(500, user.getCharacter().getGold());
        assertEquals(user.getCharacter().getInventoryCapacity(), user.getCharacter().getInventoryUsage());
    }

    private User createUserWithCharacter(String email, String characterName) {
        User user = new User(characterName.toLowerCase(), email, "hash");
        user.assignCharacter(new Character(characterName));
        return user;
    }

    private void levelUpTo(Character character, int targetLevel) {
        int totalExperience = 0;

        for (int level = 1; level < targetLevel; level++) {
            totalExperience += 100 * level;
        }

        character.gainExperience(totalExperience);
    }
}
