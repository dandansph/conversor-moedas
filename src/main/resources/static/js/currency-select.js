/** Dropdown customizado com busca e bandeiras. */

const CHEVRON =
  '<svg class="chev" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m6 9 6 6 6-6"/></svg>';

function flagUrl(code) {
  const country = code === "EUR" ? "eu" : code.slice(0, 2).toLowerCase();
  return `https://flagcdn.com/w40/${country}.png`;
}

function flagImg(code) {
  return `<img class="flag" src="${flagUrl(code)}" alt="" loading="lazy" onerror="this.style.visibility='hidden'">`;
}

export class CurrencySelect {
  #root;
  #currencies;
  #value;
  #onChange;
  #button;
  #menu;
  #search;
  #list;

  constructor(root, currencies, value, onChange) {
    this.#root = root;
    this.#currencies = Object.entries(currencies).sort(([a], [b]) => a.localeCompare(b));
    this.#value = value;
    this.#onChange = onChange;
    this.#render();
    document.addEventListener("click", (event) => {
      if (!this.#root.contains(event.target)) this.#close();
    });
    document.addEventListener("keydown", (event) => {
      if (event.key === "Escape") this.#close();
    });
  }

  get value() {
    return this.#value;
  }

  set value(code) {
    this.#value = code;
    this.#renderButton();
  }

  #render() {
    this.#root.innerHTML = `
      <button type="button" class="select-btn" aria-haspopup="listbox"></button>
      <div class="menu" hidden>
        <input class="menu-search" type="text" placeholder="Buscar moeda..." aria-label="Buscar moeda">
        <ul class="options" role="listbox"></ul>
      </div>`;
    this.#button = this.#root.querySelector(".select-btn");
    this.#menu = this.#root.querySelector(".menu");
    this.#search = this.#root.querySelector(".menu-search");
    this.#list = this.#root.querySelector(".options");

    this.#button.addEventListener("click", () => (this.#menu.hidden ? this.#open() : this.#close()));
    this.#search.addEventListener("input", () => this.#renderOptions(this.#search.value));
    this.#list.addEventListener("click", (event) => {
      const option = event.target.closest(".option");
      if (option) this.#select(option.dataset.code);
    });
    this.#search.addEventListener("keydown", (event) => {
      if (event.key === "Enter") this.#list.querySelector(".option")?.click();
    });

    this.#renderButton();
  }

  #renderButton() {
    const name = this.#currencies.find(([code]) => code === this.#value)?.[1] ?? "";
    this.#button.innerHTML = `
      ${flagImg(this.#value)}
      <span class="select-text">
        <span class="select-code">${this.#value}</span>
        <span class="select-name">${name}</span>
      </span>
      ${CHEVRON}`;
  }

  #renderOptions(filter = "") {
    const term = filter.trim().toLowerCase();
    const matches = this.#currencies.filter(
      ([code, name]) => code.toLowerCase().includes(term) || name.toLowerCase().includes(term)
    );
    this.#list.innerHTML = matches.length
      ? matches
          .map(
            ([code, name]) => `
              <li class="option${code === this.#value ? " active" : ""}" role="option" data-code="${code}">
                ${flagImg(code)}
                <span><b>${code}</b> <span class="select-name">${name}</span></span>
              </li>`
          )
          .join("")
      : '<li class="empty">Nenhuma moeda encontrada</li>';
  }

  #open() {
    this.#root.classList.add("open");
    this.#menu.hidden = false;
    this.#search.value = "";
    this.#renderOptions();
    this.#search.focus();
    this.#list.querySelector(".active")?.scrollIntoView({ block: "center" });
  }

  #close() {
    this.#root.classList.remove("open");
    this.#menu.hidden = true;
  }

  #select(code) {
    this.#value = code;
    this.#renderButton();
    this.#close();
    this.#onChange(code);
  }
}
