/**
 * api.js — Harvest Hall Frontend Utility Layer
 *
 * Centralised API calls, cart state management, and shared helpers.
 * All pages import this via <script src="../js/api.js"></script>
 */

const API_BASE = 'http://localhost:8080/api';

// ============================================================
// HTTP helpers
// ============================================================

async function apiGet(path) {
  const res = await fetch(API_BASE + path);
  if (!res.ok) throw new Error(await extractError(res));
  return res.json();
}

async function apiPost(path, body) {
  const res = await fetch(API_BASE + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(await extractError(res));
  return res.json();
}

async function apiPut(path, body) {
  const res = await fetch(API_BASE + path, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(await extractError(res));
  return res.json();
}

async function apiPatch(path) {
  const res = await fetch(API_BASE + path, { method: 'PATCH' });
  if (!res.ok) throw new Error(await extractError(res));
  return res.json();
}

async function apiDelete(path) {
  const res = await fetch(API_BASE + path, { method: 'DELETE' });
  if (!res.ok) throw new Error(await extractError(res));
}

async function extractError(res) {
  try {
    const data = await res.json();
    return data.error || data.message || 'Request failed';
  } catch {
    return 'Request failed';
  }
}

// ============================================================
// Cart — persisted in sessionStorage so it survives navigation
// ============================================================

const Cart = (() => {
  const KEY = 'hh_cart';

  function load() {
    try { return JSON.parse(sessionStorage.getItem(KEY)) || []; }
    catch { return []; }
  }

  function save(items) {
    sessionStorage.setItem(KEY, JSON.stringify(items));
  }

  function getItems() { return load(); }

  function addItem(menuItem) {
    const items = load();
    const existing = items.find(i => i.id === menuItem.id);
    if (existing) {
      existing.quantity += 1;
    } else {
      items.push({ id: menuItem.id, name: menuItem.name, price: menuItem.price, quantity: 1 });
    }
    save(items);
    _fireChange();
  }

  function removeItem(menuItemId) {
    const items = load().filter(i => i.id !== menuItemId);
    save(items);
    _fireChange();
  }

  function updateQuantity(menuItemId, delta) {
    const items = load();
    const item = items.find(i => i.id === menuItemId);
    if (!item) return;
    item.quantity += delta;
    if (item.quantity <= 0) {
      save(items.filter(i => i.id !== menuItemId));
    } else {
      save(items);
    }
    _fireChange();
  }

  function getTotal() {
    return load().reduce((sum, i) => sum + i.price * i.quantity, 0);
  }

  function getCount() {
    return load().reduce((sum, i) => sum + i.quantity, 0);
  }

  function clear() { sessionStorage.removeItem(KEY); _fireChange(); }

  // Simple pub/sub so cart panel can react
  const listeners = [];
  function onChange(fn) { listeners.push(fn); }
  function _fireChange() { listeners.forEach(fn => fn()); }

  return { getItems, addItem, removeItem, updateQuantity, getTotal, getCount, clear, onChange };
})();

// ============================================================
// Toast notification
// ============================================================

function showToast(message, duration = 3000) {
  let toast = document.getElementById('toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'toast';
    document.body.appendChild(toast);
  }
  toast.textContent = message;
  toast.classList.add('show');
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => toast.classList.remove('show'), duration);
}

// ============================================================
// Simple SPA router — shows/hides .page divs by hash
// ============================================================

function initRouter() {
  function navigate() {
    const hash = location.hash || '#menu';
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    const target = document.querySelector(hash + '-page') || document.querySelector('#menu-page');
    if (target) target.classList.add('active');
  }
  window.addEventListener('hashchange', navigate);
  navigate();
}

// ============================================================
// Format helpers
// ============================================================

function formatPrice(n) { return '$' + Number(n).toFixed(2); }

function formatDateTime(iso) {
  if (!iso) return '—';
  const clean = iso.replace('T', ' ').substring(0, 16);
  return clean;
}
