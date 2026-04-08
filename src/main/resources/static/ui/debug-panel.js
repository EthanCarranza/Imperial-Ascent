const state = { activeEmail: null, activeName: null };

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
  setText("strengthValue", sheet.strength);
  setText("intelligenceValue", sheet.intelligence);
  setText("agilityValue", sheet.agility);
  setText("luckValue", sheet.luck);
  setText("activeStatus", `${sheet.name} active`);

  setFill("xpMeter", sheet.experience, sheet.experienceRequiredForNextLevel);
  setFill("energyMeter", sheet.currentEnergy, sheet.maxEnergy);
  setFill("goldMeter", Math.min(sheet.gold, 100), 100);

  document.querySelectorAll("[data-active-character='true']").forEach((input) => {
    input.value = sheet.email;
  });
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
        `${payload.name} loaded.\nLevel ${payload.level}\nGold ${payload.gold}\nSTR ${payload.strength} | INT ${payload.intelligence} | AGI ${payload.agility} | LUCK ${payload.luck}`,
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

document.addEventListener("DOMContentLoaded", () => {
  getById("lookupForm")?.addEventListener("submit", loadCharacterFromForm);

  document.querySelectorAll("[data-debug-form='true']").forEach((form) => {
    form.addEventListener("submit", submitDebugForm);
  });

  document
    .querySelector("[data-clear-console='true']")
    ?.addEventListener("click", clearConsole);
});
