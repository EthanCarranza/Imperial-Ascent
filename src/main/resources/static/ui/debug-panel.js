const state = {
  activeEmail: null,
  activeName: null,
  shopLevel: null,
  explorationZones: [],
  explorationZoneId: null,
};
const defaultEquipmentSlots = [
  { slot: "weapon", displayName: "Weapon", itemName: null, level: 0, strengthBonus: 0, intelligenceBonus: 0, agilityBonus: 0, luckBonus: 0, weaponDamage: 0, armor: 0 },
  { slot: "armor", displayName: "Armor", itemName: null, level: 0, strengthBonus: 0, intelligenceBonus: 0, agilityBonus: 0, luckBonus: 0, weaponDamage: 0, armor: 0 },
  { slot: "accessory", displayName: "Accessory", itemName: null, level: 0, strengthBonus: 0, intelligenceBonus: 0, agilityBonus: 0, luckBonus: 0, weaponDamage: 0, armor: 0 },
  { slot: "helm", displayName: "Helm", itemName: null, level: 0, strengthBonus: 0, intelligenceBonus: 0, agilityBonus: 0, luckBonus: 0, weaponDamage: 0, armor: 0 },
  { slot: "ring", displayName: "Ring", itemName: null, level: 0, strengthBonus: 0, intelligenceBonus: 0, agilityBonus: 0, luckBonus: 0, weaponDamage: 0, armor: 0 },
  { slot: "relic", displayName: "Relic", itemName: null, level: 0, strengthBonus: 0, intelligenceBonus: 0, agilityBonus: 0, luckBonus: 0, weaponDamage: 0, armor: 0 },
];

const getById = (id) => document.getElementById(id);

const setText = (id, value) => {
  const element = getById(id);
  if (element) {
    element.textContent = value;
  }
};

const setFill = (id, current, max) => {
  const element = getById(id);
  if (!element) {
    return;
  }

  const safeMax = Math.max(1, Number(max) || 0);
  const percentage = Math.max(0, Math.min(100, ((Number(current) || 0) / safeMax) * 100));
  element.style.setProperty("--fill", `${percentage}%`);
};

const setStatMeta = (id, baseValue, bonusValue) => {
  const bonus = Number(bonusValue) || 0;
  const base = Number(baseValue) || 0;
  const suffix = bonus > 0 ? ` | Gear +${bonus}` : "";
  setText(id, `Base ${base}${suffix}`);
};

const initials = (name) => {
  if (!name) {
    return "?";
  }

  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join("");
};

const formatBonusPart = (label, value) => {
  const numericValue = Number(value) || 0;
  return numericValue > 0 ? `${label} +${numericValue}` : null;
};

const formatCombatSummary = (item) => {
  const parts = [
    formatBonusPart("DMG", item.weaponDamage),
    formatBonusPart("ARM", item.armor),
  ].filter(Boolean);

  return parts.length > 0 ? parts.join(" | ") : "No combat bonus";
};

const formatStatSummary = (item) => {
  const parts = [
    formatBonusPart("STR", item.strengthBonus),
    formatBonusPart("INT", item.intelligenceBonus),
    formatBonusPart("AGI", item.agilityBonus),
    formatBonusPart("LUCK", item.luckBonus),
  ].filter(Boolean);

  return parts.length > 0 ? parts.join(" | ") : "No stat bonus";
};

const rarityClassName = (rarity) => `rarity-badge--${(rarity || "common").toLowerCase()}`;

function createRarityBadge(rarity, label) {
  const badge = document.createElement("span");
  badge.className = `rarity-badge ${rarityClassName(rarity)}`;
  badge.textContent = label ?? "Common";
  return badge;
}

function getSelectedExplorationZone() {
  const select = getById("explorationZoneSelect");
  const zoneId = select?.value || state.explorationZoneId;

  return (
    state.explorationZones.find((zone) => zone.id === zoneId) ??
    state.explorationZones[0] ??
    null
  );
}

function getSelectedExplorationEncounter() {
  const zone = getSelectedExplorationZone();
  const select = getById("explorationEncounterSelect");

  if (!zone) {
    return null;
  }

  return (
    zone.encounters?.find((encounter) => encounter.id === select?.value) ??
    zone.encounters?.[0] ??
    null
  );
}

function syncExplorationZoneOptions() {
  const select = getById("explorationZoneSelect");
  if (!select) {
    return;
  }

  select.replaceChildren();

  if (!state.explorationZones.length) {
    const option = document.createElement("option");
    option.value = "";
    option.textContent = "No zones available";
    select.append(option);
    select.disabled = true;
    return;
  }

  const requestedZoneId =
    select.value || state.explorationZoneId || state.explorationZones[0].id;
  const zoneId = state.explorationZones.some((zone) => zone.id === requestedZoneId)
    ? requestedZoneId
    : state.explorationZones[0].id;

  state.explorationZoneId = zoneId;

  state.explorationZones.forEach((zone) => {
    const option = document.createElement("option");
    option.value = zone.id;
    option.textContent = zone.displayName;
    option.selected = zone.id === zoneId;
    select.append(option);
  });

  select.disabled = false;
}

function syncExplorationEncounterOptions(preferredEncounterId = null) {
  const select = getById("explorationEncounterSelect");
  const zone = getSelectedExplorationZone();

  if (!select) {
    return;
  }

  select.replaceChildren();

  if (!zone || !zone.encounters?.length) {
    const option = document.createElement("option");
    option.value = "";
    option.textContent = "No encounters available";
    select.append(option);
    select.disabled = true;
    return;
  }

  const requestedEncounterId = preferredEncounterId || select.value || zone.encounters[0].id;
  const encounterId = zone.encounters.some(
    (encounter) => encounter.id === requestedEncounterId,
  )
    ? requestedEncounterId
    : zone.encounters[0].id;

  zone.encounters.forEach((encounter) => {
    const option = document.createElement("option");
    option.value = encounter.id;
    option.textContent = `${encounter.displayName}${encounter.boss ? " | Boss" : ""}`;
    option.selected = encounter.id === encounterId;
    select.append(option);
  });

  select.disabled = false;
}

function updateExplorationPreview() {
  const zone = getSelectedExplorationZone();
  const encounter = getSelectedExplorationEncounter();

  if (!zone) {
    setText("explorationZoneName", "No zone loaded");
    setText("explorationZoneRange", "Recommended Lv. --");
    setText(
      "explorationZoneDescription",
      "Carga el catalogo para ver la primera zona de exploracion.",
    );
    setText("explorationEncounterName", "Encounter --");
    setText("explorationEncounterMeta", "Lv. --");
    setText("explorationEncounterRewards", "XP -- | Gold --");
    setText("explorationEncounterDropChance", "Drop --");
    setText("explorationEncounterStats", "STR -- | INT -- | AGI -- | LUCK --");
    setText("explorationEncounterDrops", "Loot preview unavailable.");
    return;
  }

  setText("explorationZoneName", zone.displayName);
  setText(
    "explorationZoneRange",
    `Recommended Lv. ${zone.recommendedLevelMin}-${zone.recommendedLevelMax}`,
  );
  setText("explorationZoneDescription", zone.description);

  if (!encounter) {
    setText("explorationEncounterName", "Encounter --");
    setText("explorationEncounterMeta", "Lv. --");
    setText("explorationEncounterRewards", "XP -- | Gold --");
    setText("explorationEncounterDropChance", "Drop --");
    setText("explorationEncounterStats", "STR -- | INT -- | AGI -- | LUCK --");
    setText("explorationEncounterDrops", "Loot preview unavailable.");
    return;
  }

  setText("explorationEncounterName", encounter.displayName);
  setText(
    "explorationEncounterMeta",
    `Lv. ${encounter.level} | ${encounter.boss ? "Boss" : "Standard"}`,
  );
  setText(
    "explorationEncounterRewards",
    `Win XP ${encounter.winExperience} | Lose XP ${encounter.lossExperience} | Gold ${encounter.goldReward}`,
  );
  setText("explorationEncounterDropChance", `Drop ${encounter.dropChance}%`);
  setText(
    "explorationEncounterStats",
    `STR ${encounter.strength} | INT ${encounter.intelligence} | AGI ${encounter.agility} | LUCK ${encounter.luck}`,
  );
  setText(
    "explorationEncounterDrops",
    (encounter.possibleDrops ?? []).join(" | ") || "No drops configured.",
  );
}

function updateExplorationCatalog(catalog) {
  state.explorationZones = catalog.zones ?? [];
  syncExplorationZoneOptions();
  syncExplorationEncounterOptions();
  updateExplorationPreview();
}

function updateCharacterSheet(sheet) {
  const profileCard = getById("profileCard");
  if (profileCard) {
    profileCard.classList.remove("empty");
  }

  state.activeEmail = sheet.email;
  state.activeName = sheet.name;

  setText("portraitInitials", initials(sheet.name));
  setText("profileBadge", "Character Loaded");
  setText("profileName", sheet.name);
  setText("profileSubtitle", `${sheet.username} | ${sheet.email}`);
  setText("emailValue", sheet.email);
  setText("levelValue", `Lv. ${sheet.level}`);
  setText("xpValue", `${sheet.experience} / ${sheet.experienceRequiredForNextLevel}`);
  setText("energyValue", `${sheet.currentEnergy} / ${sheet.maxEnergy}`);
  setText("goldValue", sheet.gold);
  setText("weaponDamageValue", `+${sheet.weaponDamageBonus}`);
  setText("armorValue", `+${sheet.armorBonus}`);
  setText("strengthValue", sheet.strength);
  setText("intelligenceValue", sheet.intelligence);
  setText("agilityValue", sheet.agility);
  setText("luckValue", sheet.luck);
  setStatMeta("strengthMeta", sheet.baseStrength, sheet.strengthBonus);
  setStatMeta("intelligenceMeta", sheet.baseIntelligence, sheet.intelligenceBonus);
  setStatMeta("agilityMeta", sheet.baseAgility, sheet.agilityBonus);
  setStatMeta("luckMeta", sheet.baseLuck, sheet.luckBonus);
  setText("activeStatus", `${sheet.name} active`);
  setText("inventoryUsage", `${sheet.inventoryUsage} / ${sheet.inventoryCapacity}`);
  syncShopLevelOptions(sheet.level);

  setFill("xpMeter", sheet.experience, sheet.experienceRequiredForNextLevel);
  setFill("energyMeter", sheet.currentEnergy, sheet.maxEnergy);
  setFill("goldMeter", Math.min(sheet.gold, 100), 100);
  renderEquipmentSlots(sheet.equipmentSlots ?? []);
  renderInventory(sheet.inventoryItems ?? [], sheet.inventoryCapacity ?? 0);

  document.querySelectorAll("[data-active-character='true']").forEach((input) => {
    input.value = sheet.email;
  });
}

function createSlotCard(slot) {
  const card = document.createElement("article");
  card.className = `slot-card ${slot.itemName ? "slot-card--equipped" : "slot-card--empty"}`;

  const slotLabel = document.createElement("span");
  slotLabel.className = "slot-card__slot";
  slotLabel.textContent = slot.displayName;

  const title = document.createElement("strong");
  title.textContent = slot.itemName ?? "Empty";

  const level = document.createElement("small");
  level.className = "slot-card__level";
  level.textContent = slot.itemName ? `Lv. ${slot.level}` : "No item";

  const combat = document.createElement("small");
  combat.className = "slot-card__combat";
  combat.textContent = formatCombatSummary(slot);

  const bonus = document.createElement("small");
  bonus.className = "slot-card__bonus";
  bonus.textContent = formatStatSummary(slot);

  card.append(slotLabel);

  if (slot.itemName) {
    card.append(createRarityBadge(slot.rarity, slot.rarityDisplayName));
  }

  card.append(title, level, combat, bonus);

  if (slot.itemName) {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "mini-action";
    button.dataset.unequipSlot = slot.slot;
    button.textContent = "Unequip";
    card.append(button);
  }

  return card;
}

function renderEquipmentSlots(slots) {
  const grid = getById("equipmentGrid");
  if (!grid) {
    return;
  }

  grid.replaceChildren();
  slots.forEach((slot) => {
    grid.append(createSlotCard(slot));
  });
}

function createInventoryItemCell(item) {
  const cell = document.createElement("article");
  cell.className = `inventory-cell inventory-cell--item ${item.equipped ? "inventory-cell--equipped" : ""}`;

  const slot = document.createElement("span");
  slot.className = "inventory-item__slot";
  slot.textContent = item.slotDisplayName;

  const rarity = createRarityBadge(item.rarity, item.rarityDisplayName);

  const title = document.createElement("strong");
  title.className = "inventory-item__name";
  title.textContent = item.name;

  const level = document.createElement("div");
  level.className = "inventory-item__level";
  level.textContent = `Lv. ${item.level}`;

  const bonus = document.createElement("div");
  bonus.className = "inventory-item__bonus";
  bonus.textContent = formatStatSummary(item);

  const combat = document.createElement("div");
  combat.className = "inventory-item__combat";
  combat.textContent = formatCombatSummary(item);

  const value = document.createElement("div");
  value.className = "inventory-item__value";
  value.textContent = `${item.goldValue} sell value`;

  cell.append(slot, rarity, title, level, combat, bonus, value);

  const actionRow = document.createElement("div");
  actionRow.className = "inventory-item__actions";

  if (item.equipped) {
    const status = document.createElement("div");
    status.className = "inventory-item__status";
    status.textContent = "Equipped";
    cell.append(status);
  } else {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "mini-action";
    button.dataset.equipItem = item.id;
    button.textContent = "Equip";
    actionRow.append(button);
  }

  const sellButton = document.createElement("button");
  sellButton.type = "button";
  sellButton.className = "mini-action mini-action--sell";
  sellButton.dataset.sellItem = item.id;
  sellButton.textContent = "Sell";

  const destroyButton = document.createElement("button");
  destroyButton.type = "button";
  destroyButton.className = "mini-action mini-action--danger";
  destroyButton.dataset.destroyItem = item.id;
  destroyButton.textContent = "Destroy";

  actionRow.append(sellButton, destroyButton);
  cell.append(actionRow);

  return cell;
}

function createEmptyInventoryCell() {
  const cell = document.createElement("div");
  cell.className = "inventory-cell inventory-cell--empty";
  cell.textContent = "Empty";
  return cell;
}

function renderInventory(items, capacity) {
  const grid = getById("inventoryGrid");
  if (!grid) {
    return;
  }

  grid.replaceChildren();

  items.forEach((item) => {
    grid.append(createInventoryItemCell(item));
  });

  const emptySlots = Math.max(0, capacity - items.length);
  for (let index = 0; index < emptySlots; index += 1) {
    grid.append(createEmptyInventoryCell());
  }
}

function syncShopLevelOptions(characterLevel) {
  const select = getById("shopLevelSelect");
  if (!select) {
    return;
  }

  const safeCharacterLevel = Math.max(1, Number(characterLevel) || 1);
  const currentLevel =
    Number(select.value) || Number(state.shopLevel) || safeCharacterLevel;
  const nextLevel = Math.min(safeCharacterLevel, Math.max(1, currentLevel));

  select.replaceChildren();

  for (let level = safeCharacterLevel; level >= 1; level -= 1) {
    const option = document.createElement("option");
    option.value = String(level);
    option.textContent = `Lv. ${level}`;
    if (level === nextLevel) {
      option.selected = true;
    }
    select.append(option);
  }

  select.disabled = false;
  state.shopLevel = nextLevel;
  setText("shopLevelCap", `Level cap Lv. ${safeCharacterLevel}`);
}

function createShopOfferCard(offer) {
  const card = document.createElement("article");
  card.className = "shop-card";

  const slot = document.createElement("span");
  slot.className = "shop-card__slot";
  slot.textContent = offer.slotDisplayName;

  const rarity = createRarityBadge(offer.rarity, offer.rarityDisplayName);

  const title = document.createElement("strong");
  title.className = "shop-card__name";
  title.textContent = offer.name;

  const level = document.createElement("div");
  level.className = "shop-card__level";
  level.textContent = `Lv. ${offer.level}`;

  const combat = document.createElement("div");
  combat.className = "shop-card__combat";
  combat.textContent = formatCombatSummary(offer);

  const bonus = document.createElement("div");
  bonus.className = "shop-card__bonus";
  bonus.textContent = formatStatSummary(offer);

  const price = document.createElement("div");
  price.className = "shop-card__price";
  price.textContent = `${offer.price} gold`;

  const value = document.createElement("div");
  value.className = "shop-card__value";
  value.textContent = `Value ${offer.goldValue}`;

  const button = document.createElement("button");
  button.type = "button";
  button.className = "mini-action";
  button.dataset.buyPreset = offer.presetId;
  button.dataset.buyLevel = offer.level;
  button.disabled = !offer.affordable;
  button.textContent = offer.affordable ? "Buy" : "Need Gold";

  card.append(slot, rarity, title, level, combat, bonus, price, value, button);
  return card;
}

function renderShopEmpty(message) {
  const grid = getById("shopGrid");
  if (!grid) {
    return;
  }

  grid.replaceChildren();

  const emptyState = document.createElement("div");
  emptyState.className = "shop-empty";
  emptyState.textContent = message;
  grid.append(emptyState);
}

function renderShopOffers(offers) {
  const grid = getById("shopGrid");
  if (!grid) {
    return;
  }

  grid.replaceChildren();

  if (!offers.length) {
    renderShopEmpty("No hay ofertas para este nivel.");
    return;
  }

  offers.forEach((offer) => {
    grid.append(createShopOfferCard(offer));
  });
}

function updateShopCatalog(catalog) {
  state.shopLevel = catalog.selectedLevel;
  setText("shopSummary", `Vendor stock comun para Lv. ${catalog.selectedLevel}.`);
  setText("shopGoldStatus", `${catalog.gold} gold`);

  const select = getById("shopLevelSelect");
  if (select) {
    select.value = String(catalog.selectedLevel);
  }

  renderShopOffers(catalog.offers ?? []);
}

async function loadExplorationZones(options = {}) {
  const { silent = true } = options;

  try {
    const response = await fetch("/api/debug/exploration-zones");
    const payload = await response.json();

    if (!response.ok) {
      if (!silent) {
        appendOutput(
          "Exploration Catalog",
          payload.message ?? "Exploration catalog unavailable",
          "error",
        );
      }
      return;
    }

    updateExplorationCatalog(payload);
  } catch (error) {
    if (!silent) {
      appendOutput("Exploration Catalog", `Error: ${error}`, "error");
    }
  }
}

function appendOutput(title, text, tone = "info") {
  const output = getById("output");
  if (!output) {
    return;
  }

  if (output.dataset.empty === "true") {
    output.replaceChildren();
    output.dataset.empty = "false";
  }

  const entry = document.createElement("article");
  entry.className = `entry ${tone}`;

  const head = document.createElement("div");
  head.className = "entry-head";

  const titleElement = document.createElement("span");
  titleElement.textContent = title;

  const timeElement = document.createElement("span");
  timeElement.textContent = new Date().toLocaleTimeString();

  const body = document.createElement("pre");
  body.textContent = text;

  head.append(titleElement, timeElement);
  entry.append(head, body);
  output.prepend(entry);
}

function clearConsole() {
  const output = getById("output");
  if (!output) {
    return;
  }

  output.replaceChildren();
  output.dataset.empty = "true";

  const emptyState = document.createElement("div");
  emptyState.className = "empty-state";
  emptyState.textContent = "Consola limpia. La siguiente accion aparecera aqui.";
  output.append(emptyState);
}

async function loadCharacter(email, options = {}) {
  const { log = true, silent = false } = options;

  try {
    const response = await fetch(
      `/api/debug/character-sheet?email=${encodeURIComponent(email)}`,
    );
    const payload = await response.json();

    if (!response.ok) {
      if (!silent) {
        appendOutput(
          "Character Lookup",
          payload.message ?? "Character not found",
          "error",
        );
      }
      return;
    }

    updateCharacterSheet(payload);
    await loadShopOffers(payload.email, { silent: true, log: false });

    if (log) {
      appendOutput(
        "Character Lookup",
        `${payload.name} loaded.\nLevel ${payload.level}\nGold ${payload.gold}\nInventory ${payload.inventoryUsage}/${payload.inventoryCapacity}\nSTR ${payload.strength} | INT ${payload.intelligence} | AGI ${payload.agility} | LUCK ${payload.luck}`,
      );
    }
  } catch (error) {
    if (!silent) {
      appendOutput("Character Lookup", `Error: ${error}`, "error");
    }
  }
}

async function loadShopOffers(email, options = {}) {
  const { silent = true, log = false } = options;

  if (!email) {
    return;
  }

  const selectedLevel =
    Number(getById("shopLevelSelect")?.value || state.shopLevel || 0) || null;
  const query = new URLSearchParams({ email });

  if (selectedLevel) {
    query.set("level", String(selectedLevel));
  }

  try {
    const response = await fetch(`/api/debug/shop-offers?${query.toString()}`);
    const payload = await response.json();

    if (!response.ok) {
      if (!silent) {
        appendOutput("Shop Offers", payload.message ?? "Shop unavailable", "error");
      }
      return;
    }

    updateShopCatalog(payload);

    if (log) {
      appendOutput(
        "Shop Offers",
        `${payload.offers.length} common offers for Lv. ${payload.selectedLevel}.\nGold ${payload.gold}`,
      );
    }
  } catch (error) {
    if (!silent) {
      appendOutput("Shop Offers", `Error: ${error}`, "error");
    }
  }
}

async function loadCharacterFromForm(event) {
  event.preventDefault();

  const email = new FormData(event.currentTarget).get("email");
  if (!email) {
    appendOutput(
      "Character Lookup",
      "Introduce un email para cargar el personaje.",
      "error",
    );
    return;
  }

  await loadCharacter(email);
}

async function maybeRefreshActive(form, url) {
  if (url.endsWith("/create-character")) {
    const email = new FormData(form).get("email");
    if (email) {
      await loadCharacter(email, { log: false, silent: true });
    }
    return;
  }

  if (!state.activeEmail) {
    return;
  }

  const formData = new FormData(form);
  if (
    formData.get("email") === state.activeEmail ||
    formData.get("attackerEmail") === state.activeEmail
  ) {
    await loadCharacter(state.activeEmail, { log: false, silent: true });
  }
}

async function submitDebugForm(event) {
  event.preventDefault();

  const form = event.currentTarget;
  const url = form.getAttribute("action");
  const method = (form.getAttribute("method") || "post").toUpperCase();
  const params = new URLSearchParams(new FormData(form));
  const label = form.dataset.logLabel || "Debug Action";

  try {
    const response =
      method === "GET"
        ? await fetch(`${url}?${params.toString()}`)
        : await fetch(url, { method, body: params });

    appendOutput(
      label,
      await response.text(),
      response.ok ? "info" : "error",
    );

    await maybeRefreshActive(form, url);
  } catch (error) {
    appendOutput(label, `Error: ${error}`, "error");
  }
}

async function runInventoryAction(url, params, label) {
  if (!state.activeEmail) {
    appendOutput(label, "Load a character first.", "error");
    return;
  }

  try {
    const response = await fetch(url, {
      method: "POST",
      body: new URLSearchParams(params),
    });

    appendOutput(
      label,
      await response.text(),
      response.ok ? "info" : "error",
    );

    await loadCharacter(state.activeEmail, { log: false, silent: true });
  } catch (error) {
    appendOutput(label, `Error: ${error}`, "error");
  }
}

function handleInventoryClick(event) {
  const buyButton = event.target.closest("[data-buy-preset]");
  if (buyButton) {
    runInventoryAction(
      "/api/debug/buy-item",
      {
        email: state.activeEmail,
        preset: buyButton.dataset.buyPreset,
        level: buyButton.dataset.buyLevel,
      },
      "Buy Item",
    );
    return;
  }

  const sellButton = event.target.closest("[data-sell-item]");
  if (sellButton) {
    runInventoryAction(
      "/api/debug/sell-item",
      {
        email: state.activeEmail,
        itemId: sellButton.dataset.sellItem,
      },
      "Sell Item",
    );
    return;
  }

  const destroyButton = event.target.closest("[data-destroy-item]");
  if (destroyButton) {
    const confirmed = window.confirm(
      "Destroy this item permanently? Selling is usually better.",
    );

    if (!confirmed) {
      return;
    }

    runInventoryAction(
      "/api/debug/destroy-item",
      {
        email: state.activeEmail,
        itemId: destroyButton.dataset.destroyItem,
      },
      "Destroy Item",
    );
    return;
  }

  const equipButton = event.target.closest("[data-equip-item]");
  if (equipButton) {
    runInventoryAction(
      "/api/debug/equip-item",
      {
        email: state.activeEmail,
        itemId: equipButton.dataset.equipItem,
      },
      "Equip Item",
    );
    return;
  }

  const unequipButton = event.target.closest("[data-unequip-slot]");
  if (unequipButton) {
    runInventoryAction(
      "/api/debug/unequip-slot",
      {
        email: state.activeEmail,
        slot: unequipButton.dataset.unequipSlot,
      },
      "Unequip Slot",
    );
  }
}

function handleShopLevelChange(event) {
  const nextLevel = Number(event.currentTarget.value) || null;
  state.shopLevel = nextLevel;

  if (!state.activeEmail) {
    return;
  }

  loadShopOffers(state.activeEmail, { silent: false, log: false });
}

function handleExplorationZoneChange() {
  const zone = getSelectedExplorationZone();
  state.explorationZoneId = zone?.id ?? null;
  syncExplorationEncounterOptions();
  updateExplorationPreview();
}

function handleExplorationEncounterChange() {
  updateExplorationPreview();
}

document.addEventListener("DOMContentLoaded", () => {
  renderEquipmentSlots(defaultEquipmentSlots);
  renderInventory([], 12);
  renderShopEmpty("Carga un personaje para ver ofertas comunes.");
  setText("inventoryUsage", "0 / 12");
  setText("shopSummary", "Carga un personaje para ver la tienda.");
  setText("shopGoldStatus", "Gold --");
  updateExplorationPreview();

  getById("lookupForm")?.addEventListener("submit", loadCharacterFromForm);
  getById("shopLevelSelect")?.addEventListener("change", handleShopLevelChange);
  getById("explorationZoneSelect")?.addEventListener("change", handleExplorationZoneChange);
  getById("explorationEncounterSelect")
    ?.addEventListener("change", handleExplorationEncounterChange);

  document.querySelectorAll("[data-debug-form='true']").forEach((form) => {
    form.addEventListener("submit", submitDebugForm);
  });

  document
    .querySelector("[data-clear-console='true']")
    ?.addEventListener("click", clearConsole);

  document.addEventListener("click", handleInventoryClick);
  loadExplorationZones({ silent: false });
});
