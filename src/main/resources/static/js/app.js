import { convert, fetchCurrencies } from "./api.js";
import { CurrencySelect } from "./currency-select.js";
import { currencySymbol, formatDate, formatMoney, formatRate, parseAmount } from "./format.js";

const DEFAULTS = { from: "USD", to: "BRL" };
const POPULAR_PAIRS = [["USD", "BRL"], ["EUR", "BRL"], ["GBP", "BRL"], ["BRL", "USD"], ["EUR", "USD"], ["USD", "JPY"]];
const HISTORY_KEY = "conversor.history";
const HISTORY_LIMIT = 5;
const DEBOUNCE_MS = 350;

const $ = (id) => document.getElementById(id);
const els = {
  amount: $("amount"),
  symbol: $("amountSymbol"),
  swap: $("swap"),
  result: $("result"),
  label: $("resultLabel"),
  value: $("resultValue"),
  meta: $("resultMeta"),
  copy: $("copy"),
  error: $("error"),
  chips: $("chips"),
  historyBox: $("historyBox"),
  history: $("history"),
  clearHistory: $("clearHistory"),
};

let fromSelect;
let toSelect;
let lastResult = null;
let requestId = 0;
let debounceTimer;

/* ---------- Conversão ---------- */

function setLoading(loading) {
  els.result.classList.toggle("loading", loading);
}

function showError(message) {
  els.error.textContent = message;
  els.error.hidden = !message;
}

function renderResult(data) {
  lastResult = data;
  els.label.textContent = `${formatMoney(data.valorOriginal, data.de)} =`;
  els.value.textContent = formatMoney(data.valorConvertido, data.para);
  els.meta.innerHTML =
    `1 ${data.de} = <b>${formatRate(data.taxa)} ${data.para}</b><br>Cotação de ${formatDate(data.dataCotacao)}`;
}

async function runConversion() {
  const amount = parseAmount(els.amount.value);
  if (!(amount > 0)) {
    showError("Digite um valor maior que zero.");
    return;
  }

  const id = ++requestId;
  setLoading(true);
  showError("");
  try {
    const data = await convert(fromSelect.value, toSelect.value, amount);
    if (id !== requestId) return; // resposta antiga: já existe uma requisição mais nova
    renderResult(data);
    saveHistory(data);
  } catch (error) {
    if (id === requestId) showError(error.message);
  } finally {
    if (id === requestId) setLoading(false);
  }
}

function scheduleConversion() {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(runConversion, DEBOUNCE_MS);
}

function onCurrencyChange() {
  els.symbol.textContent = currencySymbol(fromSelect.value);
  runConversion();
}

/* ---------- Histórico ---------- */

function loadHistory() {
  try {
    return JSON.parse(localStorage.getItem(HISTORY_KEY)) ?? [];
  } catch {
    return [];
  }
}

function saveHistory(data) {
  const entry = { de: data.de, para: data.para, valor: data.valorOriginal, resultado: data.valorConvertido };
  const history = loadHistory().filter((h) => !(h.de === entry.de && h.para === entry.para && h.valor === entry.valor));
  history.unshift(entry);
  try {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(history.slice(0, HISTORY_LIMIT)));
  } catch {
    /* armazenamento indisponível: o histórico é opcional */
  }
  renderHistory();
}

function renderHistory() {
  const history = loadHistory();
  els.historyBox.hidden = history.length === 0;
  els.history.innerHTML = history
    .map((h) => `<li><span>${formatMoney(h.valor, h.de)}</span><span>${formatMoney(h.resultado, h.para)}</span></li>`)
    .join("");
}

/* ---------- Inicialização ---------- */

function setupEvents() {
  els.amount.addEventListener("input", scheduleConversion);

  els.swap.addEventListener("click", () => {
    const from = fromSelect.value;
    fromSelect.value = toSelect.value;
    toSelect.value = from;
    onCurrencyChange();
  });

  els.chips.innerHTML = POPULAR_PAIRS
    .map(([from, to]) => `<button type="button" class="chip" data-from="${from}" data-to="${to}">${from} → ${to}</button>`)
    .join("");
  els.chips.addEventListener("click", (event) => {
    const chip = event.target.closest(".chip");
    if (!chip) return;
    fromSelect.value = chip.dataset.from;
    toSelect.value = chip.dataset.to;
    onCurrencyChange();
  });

  els.copy.addEventListener("click", async () => {
    if (!lastResult) return;
    try {
      await navigator.clipboard.writeText(formatMoney(lastResult.valorConvertido, lastResult.para));
      els.copy.textContent = "Copiado!";
      setTimeout(() => (els.copy.textContent = "Copiar resultado"), 1500);
    } catch {
      showError("Não foi possível copiar o resultado.");
    }
  });

  els.clearHistory.addEventListener("click", () => {
    try {
      localStorage.removeItem(HISTORY_KEY);
    } catch {
      /* ignora */
    }
    renderHistory();
  });
}

async function init() {
  setLoading(true);
  try {
    const currencies = await fetchCurrencies();
    fromSelect = new CurrencySelect($("fromSelect"), currencies, DEFAULTS.from, onCurrencyChange);
    toSelect = new CurrencySelect($("toSelect"), currencies, DEFAULTS.to, onCurrencyChange);
    setupEvents();
    renderHistory();
    onCurrencyChange();
  } catch (error) {
    setLoading(false);
    showError(error.message);
  }
}

init();
