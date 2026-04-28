function qs(sel){return document.querySelector(sel);}
function qsa(sel){return [...document.querySelectorAll(sel)];}

function getParam(name, fallback){
  const v = new URLSearchParams(location.search).get(name);
  if (v === null || v === '') return fallback;
  return v;
}

async function fetchJson(url, options){
  const res = await fetch(url, options);
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    const message = data && data.message ? data.message : `${res.status} ${res.statusText}`;
    const err = new Error(message);
    err.status = res.status;
    err.data = data;
    throw err;
  }
  return data;
}

function formatDateTime(dt){
  if (!dt) return '-';
  // backend sends ISO local datetime
  return String(dt).replace('T',' ');
}

function renderNav(active){
  const nav = document.createElement('div');
  nav.className = 'nav';
  nav.innerHTML = `
    <div class="nav-inner">
      <a href="/safety.html" data-key="safety"><span class="icon">🛡️</span>安全</a>
      <a href="/health.html" data-key="health"><span class="icon">📈</span>健康</a>
      <a href="/volunteer.html" data-key="volunteer"><span class="icon">🤝</span>志愿</a>
      <a href="/profile.html" data-key="profile"><span class="icon">👤</span>个人</a>
    </div>
  `;
  document.body.appendChild(nav);
  qsa('.nav a').forEach(a => {
    if (a.dataset.key === active) a.classList.add('active');
  });
}

function setSegmentActive(containerSel, value){
  qsa(containerSel + ' button').forEach(b => {
    b.classList.toggle('active', b.dataset.value === value);
  });
}

function todayStr(){
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth()+1).padStart(2,'0');
  const day = String(d.getDate()).padStart(2,'0');
  return `${y}-${m}-${day}`;
}

function nowStr(){
  const d = new Date();
  const hh = String(d.getHours()).padStart(2,'0');
  const mm = String(d.getMinutes()).padStart(2,'0');
  return `${todayStr()} ${hh}:${mm}`;
}

function lsGet(key, fallback){
  try{
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  }catch{
    return fallback;
  }
}

function lsSet(key, value){
  localStorage.setItem(key, JSON.stringify(value));
}
