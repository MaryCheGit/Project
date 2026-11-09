// Configuration
const API_BASE = '/api';

// Auth
function saveAuth(token, user) {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
}

function getToken() {
    return localStorage.getItem('token');
}

function getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

function isAuthenticated() {
    return !!getToken();
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/';
}

// API Request
async function apiRequest(url, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    const token = getToken();
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    const response = await fetch(API_BASE + url, {
        ...options,
        headers
    });

    if (response.status === 401 || response.status === 403) {
        if (!url.startsWith('/auth/')) {
            logout();
            throw new Error('Unauthorized');
        }
    }

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    if (!text || text.trim() === '') {
        return null;
    }

    let data;
    try {
        data = JSON.parse(text);
    } catch (e) {
        throw new Error('Invalid JSON response');
    }

    if (!response.ok) {
        throw new Error(data.message || 'Request failed');
    }

    return data;
}

async function apiGet(url) {
    return apiRequest(url, { method: 'GET' });
}

async function apiPost(url, body) {
    return apiRequest(url, {
        method: 'POST',
        body: JSON.stringify(body)
    });
}

async function apiPut(url, body) {
    return apiRequest(url, {
        method: 'PUT',
        body: JSON.stringify(body)
    });
}

async function apiDelete(url) {
    return apiRequest(url, { method: 'DELETE' });
}

// Update header
function updateHeader() {
    const loginBtn = document.getElementById('loginBtn');
    const profileBtn = document.getElementById('profileBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    if (!loginBtn && !profileBtn && !logoutBtn) return;

    const user = getCurrentUser();

    if (isAuthenticated()) {
        if (loginBtn) loginBtn.classList.add('hidden');
        if (profileBtn) profileBtn.classList.remove('hidden');
        if (logoutBtn) logoutBtn.classList.remove('hidden');

        if (profileBtn && user) {
            profileBtn.textContent = '👤 ' + (user.fullName || user.username);
        }

        // Показываем ссылку "Админ-панель" только админам
        document.querySelectorAll('a[href="/admin.html"]').forEach(link => {
            if (user && user.role === 'ADMIN') {
                link.classList.remove('hidden');
            } else {
                link.classList.add('hidden');
            }
        });
    } else {
        if (loginBtn) loginBtn.classList.remove('hidden');
        if (profileBtn) profileBtn.classList.add('hidden');
        if (logoutBtn) logoutBtn.classList.add('hidden');

        document.querySelectorAll('a[href="/admin.html"]').forEach(link => {
            link.classList.add('hidden');
        });
    }
}

// Stars
function renderStars(rating) {
    if (!rating) return '☆☆☆☆☆';
    const fullStars = Math.floor(rating);
    const hasHalf = rating - fullStars >= 0.5;
    let stars = '★'.repeat(fullStars);
    if (hasHalf) stars += '⯨';
    stars += '☆'.repeat(5 - fullStars - (hasHalf ? 1 : 0));
    return stars;
}

// Declension
function declOfNum(number, titles) {
    const cases = [2, 0, 1, 1, 1, 2];
    return titles[(number % 100 > 4 && number % 100 < 20)
        ? 2
        : cases[(number % 10 < 5) ? number % 10 : 5]];
}

// Toast notification
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.style.cssText = 'position: fixed; bottom: 24px; right: 24px; background: ' +
        (type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#6366f1') +
        '; color: white; padding: 16px 24px; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); font-weight: 500; font-size: 15px; z-index: 9999; opacity: 0; transform: translateY(20px); transition: all 0.3s ease; max-width: 400px;';
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
    }, 10);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
        setTimeout(() => {
            if (toast.parentNode) document.body.removeChild(toast);
        }, 300);
    }, 3000);
}

// Image fallback
function imgFallback(img, emoji) {
    img.onerror = function() {
        const parent = this.parentElement;
        parent.innerHTML = '<span style="font-size: 100px;">' + emoji + '</span>';
    };
}

// LIBRARY: add
async function addToLibraryAndUpdate(type, id) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    const user = getCurrentUser();
    const body = {
        user: { id: user.userId },
        status: type === 'book' ? 'WANT_TO_READ' : 'WANT_TO_WATCH'
    };

    if (type === 'book') {
        body.book = { id: id };
    } else {
        body.movie = { id: id };
    }

    try {
        await apiPost('/library', body);
        showToast('Добавлено в библиотеку!', 'success');
        setTimeout(() => window.location.reload(), 500);
    } catch (error) {
        showToast('Ошибка: ' + error.message, 'error');
    }
}

// LIBRARY: remove
async function removeFromLibrary(entryId, type, entityId) {
    if (!confirm('Удалить из библиотеки?')) return;

    try {
        await apiDelete('/library/' + entryId);
        showToast('Удалено из библиотеки', 'success');
        setTimeout(() => window.location.reload(), 500);
    } catch (error) {
        showToast('Ошибка: ' + error.message, 'error');
    }
}

// RATE: красивое модальное окно
async function rateItem(type, id) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    let existingRating = null;
    try {
        const ratings = await apiGet('/ratings/' + type + '/' + id);
        const user = getCurrentUser();
        const found = ratings.find(r => r.user.id === user.userId);
        existingRating = found || null;
    } catch (error) {
        // ignore
    }

    const modal = document.createElement('div');
    modal.id = 'ratingModal';
    modal.style.cssText = 'position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; z-index: 1000; backdrop-filter: blur(4px);';

    const isEdit = !!existingRating;
    const plotVal = isEdit ? existingRating.plotScore : 5;
    const charVal = isEdit ? existingRating.characterScore : 5;
    const overVal = isEdit ? existingRating.overallScore : 5;
    const commentVal = isEdit ? (existingRating.comment || '') : '';

    modal.innerHTML = `
        <div style="background: white; border-radius: 20px; padding: 40px; max-width: 480px; width: 90%; box-shadow: 0 20px 60px rgba(0,0,0,0.3);">
            <h2 style="font-size: 24px; font-weight: 800; margin-bottom: 8px; text-align: center;">
                ${isEdit ? 'Изменить оценку' : 'Оценить'}
            </h2>
            <p style="color: var(--color-text-secondary); text-align: center; margin-bottom: 28px; font-size: 14px;">
                Поделитесь своим мнением
            </p>

            <div style="margin-bottom: 20px;">
                <label style="display: block; margin-bottom: 8px; font-weight: 600; font-size: 14px;">Сюжет (1-5)</label>
                <input type="number" id="plotScore" min="1" max="5" value="${plotVal}" style="width: 100%; padding: 12px 16px; border: 1px solid #e5e7eb; border-radius: 8px; font-size: 15px; font-family: inherit;">
            </div>

            <div style="margin-bottom: 20px;">
                <label style="display: block; margin-bottom: 8px; font-weight: 600; font-size: 14px;">Персонажи (1-5)</label>
                <input type="number" id="characterScore" min="1" max="5" value="${charVal}" style="width: 100%; padding: 12px 16px; border: 1px solid #e5e7eb; border-radius: 8px; font-size: 15px; font-family: inherit;">
            </div>

            <div style="margin-bottom: 20px;">
                <label style="display: block; margin-bottom: 8px; font-weight: 600; font-size: 14px;">Общее впечатление (1-5)</label>
                <input type="number" id="overallScore" min="1" max="5" value="${overVal}" style="width: 100%; padding: 12px 16px; border: 1px solid #e5e7eb; border-radius: 8px; font-size: 15px; font-family: inherit;">
            </div>

            <div style="margin-bottom: 28px;">
                <label style="display: block; margin-bottom: 8px; font-weight: 600; font-size: 14px;">Комментарий (необязательно)</label>
                <textarea id="comment" rows="3" style="width: 100%; padding: 12px 16px; border: 1px solid #e5e7eb; border-radius: 8px; font-family: inherit; font-size: 15px; resize: vertical;" placeholder="Поделитесь впечатлениями...">${commentVal}</textarea>
            </div>

            <div style="display: flex; gap: 12px;">
                <button id="submitRating" class="form-btn" style="flex: 1;">${isEdit ? 'Обновить' : 'Отправить'}</button>
                <button id="cancelRating" class="form-btn" style="flex: 1; background: #e5e7eb; color: #1a1a2e;">Отмена</button>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    document.getElementById('cancelRating').addEventListener('click', () => {
        document.body.removeChild(modal);
    });

    document.getElementById('submitRating').addEventListener('click', async () => {
        const user = getCurrentUser();
        const body = {
            user: { id: user.userId },
            plotScore: parseInt(document.getElementById('plotScore').value),
            characterScore: parseInt(document.getElementById('characterScore').value),
            overallScore: parseInt(document.getElementById('overallScore').value),
            comment: document.getElementById('comment').value
        };

        if (type === 'book') {
            body.book = { id: id };
        } else {
            body.movie = { id: id };
        }

        try {
            await apiPost('/ratings', body);
            showToast(isEdit ? 'Оценка обновлена!' : 'Оценка сохранена!', 'success');
            document.body.removeChild(modal);
            setTimeout(() => window.location.reload(), 500);
        } catch (error) {
            showToast('Ошибка: ' + error.message, 'error');
        }
    });
}

// DOWNLOAD: имитация скачивания
function downloadFile(format, entityId, entityType) {
    const key = 'downloaded_' + entityType + '_' + entityId + '_' + format;
    const btn = document.getElementById('downloadBtn_' + format);

    if (localStorage.getItem(key)) {
        if (btn) {
            btn.textContent = '✅ ' + format + ' СКАЧАНО';
            btn.style.background = '#10b981';
            btn.disabled = true;
        }
        return;
    }

    if (btn) {
        btn.textContent = '⏳ Скачивание...';
        btn.disabled = true;
    }

    setTimeout(() => {
        localStorage.setItem(key, 'true');
        if (btn) {
            btn.textContent = '✅ ' + format + ' СКАЧАНО';
            btn.style.background = '#10b981';
            btn.disabled = true;
        }
        showToast('Файл ' + format + ' скачан!', 'success');
    }, 1500);
}

// WATCH ONLINE: модальное окно с плеером
function watchOnline(url, title) {
    const modal = document.createElement('div');
    modal.id = 'videoModal';
    modal.style.cssText = 'position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.9); display: flex; align-items: center; justify-content: center; z-index: 9999;';

    modal.innerHTML = `
        <div style="position: relative; width: 90%; max-width: 1200px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; color: white;">
                <h3 style="font-size: 20px;">${title}</h3>
                <button onclick="document.body.removeChild(document.getElementById('videoModal'))"
                        style="background: #ef4444; color: white; border: none; padding: 8px 16px; border-radius: 8px; font-size: 16px; cursor: pointer;">
                    ✕ Закрыть
                </button>
            </div>
            <div style="position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden; border-radius: 12px;">
                <iframe src="${url}"
                        style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;"
                        allowfullscreen>
                </iframe>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    const escHandler = (e) => {
        if (e.key === 'Escape') {
            const m = document.getElementById('videoModal');
            if (m) document.body.removeChild(m);
            document.removeEventListener('keydown', escHandler);
        }
    };
    document.addEventListener('keydown', escHandler);
}

// Init
document.addEventListener('DOMContentLoaded', () => {
    updateHeader();

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            logout();
        });
    }
});