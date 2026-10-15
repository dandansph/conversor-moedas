/** Cliente da API REST do próprio backend. */

async function request(url) {
  let response;
  try {
    response = await fetch(url);
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }

  const body = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(body.detail || body.message || "Erro ao processar a requisição.");
  }
  return body;
}

export function fetchCurrencies() {
  return request("/api/moedas");
}

export function convert(from, to, amount) {
  const query = new URLSearchParams({ de: from, para: to, valor: amount });
  return request(`/api/converter?${query}`);
}
