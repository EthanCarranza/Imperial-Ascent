const state = { activeEmail: null, activeName: null };
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

  card.append(slotLabel, title, level, combat, bonus);

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
  value.textContent = `${item.goldValue} gold value`;

  cell.append(slot, title, level, combat, bonus, value);

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
    cell.append(button);
  }

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

document.addEventListener("DOMContentLoaded", () => {
  renderEquipmentSlots(defaultEquipmentSlots);
  renderInventory([], 12);
  setText("inventoryUsage", "0 / 12");

  getById("lookupForm")?.addEventListener("submit", loadCharacterFromForm);

  document.querySelectorAll("[data-debug-form='true']").forEach((form) => {
    form.addEventListener("submit", submitDebugForm);
  });

  document
    .querySelector("[data-clear-console='true']")
    ?.addEventListener("click", clearConsole);

  document.addEventListener("click", handleInventoryClick);
});
