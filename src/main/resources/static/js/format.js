/** Utilitários de formatação e leitura de números. */

const LOCALE = "pt-BR";

/** Aceita "1.234,56", "1234,56" ou "1234.56" e devolve um número (ou NaN). */
export function parseAmount(text) {
  const clean = text.trim().replace(/\s/g, "");
  if (!clean) return NaN;
  const normalized = clean.includes(",") ? clean.replace(/\./g, "").replace(",", ".") : clean;
  return /^\d+(\.\d+)?$|^\.\d+$/.test(normalized) ? Number(normalized) : NaN;
}

export function formatMoney(value, currency) {
  try {
    return new Intl.NumberFormat(LOCALE, { style: "currency", currency }).format(value);
  } catch {
    return `${new Intl.NumberFormat(LOCALE, { minimumFractionDigits: 2 }).format(value)} ${currency}`;
  }
}

export function formatRate(value) {
  return new Intl.NumberFormat(LOCALE, { maximumFractionDigits: 6 }).format(value);
}

export function formatDate(isoDate) {
  const [year, month, day] = isoDate.split("-");
  return `${day}/${month}/${year}`;
}

export function currencySymbol(currency) {
  try {
    const part = new Intl.NumberFormat(LOCALE, { style: "currency", currency, currencyDisplay: "narrowSymbol" })
      .formatToParts(0)
      .find((p) => p.type === "currency");
    return part ? part.value : currency;
  } catch {
    return currency;
  }
}
