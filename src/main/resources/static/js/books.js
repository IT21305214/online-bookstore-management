const PAGE_SIZE = 8;
let allBooks = [];
let currentPage = 1;
let searchTimer;

const tableWrap = document.getElementById('tableWrap');
const tableBody = document.getElementById('bookTableBody');
const emptyMessage = document.getElementById('emptyMessage');
const tableFooter = document.getElementById('tableFooter');
const pageInfo = document.getElementById('pageInfo');
const pagination = document.getElementById('pagination');
const keywordInput = document.getElementById('keyword');
const typeFilter = document.getElementById('typeFilter');

const EDIT_ICON = '<svg class="icon" viewBox="0 0 24 24"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>';
const DELETE_ICON = '<svg class="icon" viewBox="0 0 24 24"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6M14 11v6"/></svg>';
const BOOK_ICON = '<svg class="icon" viewBox="0 0 24 24"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>';

// ---------- Helpers ----------

// Stops user-typed text from being treated as HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatPrice(value) {
    return Number(value).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

// In stock when there is at least one copy, otherwise out of stock
function stockStatus(quantity) {
    return quantity > 0
        ? { label: 'In stock', cls: 'status-in' }
        : { label: 'Out of stock', cls: 'status-out' };
}

function showAlert(message, type) {
    const alertBox = document.getElementById('alert');
    alertBox.textContent = message;
    alertBox.className = 'alert alert-' + type;
    alertBox.style.display = 'block';
    setTimeout(() => alertBox.style.display = 'none', 4000);
}

// ---------- Loading data ----------

// Totals at the top right (whole store, not affected by search)
async function loadTotals() {
    try {
        const response = await fetch('/api/books/stats');
        const stats = await response.json();
        document.getElementById('totalBooks').textContent = stats.totalBooks;
        document.getElementById('totalStock').textContent = stats.totalStock;
    } catch (error) {
        console.error('Could not load totals', error);
    }
}

// READ - get books from the backend (search is done by the Java service)
async function loadBooks(resetPage = true) {
    try {
        const keyword = keywordInput.value.trim();
        const url = keyword
            ? '/api/books?keyword=' + encodeURIComponent(keyword)
            : '/api/books';
        const response = await fetch(url);
        allBooks = await response.json();
        if (resetPage) currentPage = 1;
        render();
    } catch (error) {
        showAlert('Could not load books. Is the server running?', 'error');
    }
}

// ---------- Rendering ----------

function getFilteredBooks() {
    const type = typeFilter.value;
    return type === 'ALL' ? allBooks : allBooks.filter(book => book.type === type);
}

function rowHtml(book) {
    const status = stockStatus(book.quantity);
    const isEbook = book.type === 'EBOOK';
    const coverHtml = book.coverUrl
        ? `<img class="cover" src="${escapeHtml(book.coverUrl)}" alt="">`
        : `<div class="cover cover-empty">${BOOK_ICON}</div>`;

    return `
        <tr>
            <td class="muted nowrap">#${escapeHtml(book.id)}</td>
            <td>
                <div class="book-cell">
                    ${coverHtml}
                    <span class="book-title">${escapeHtml(book.title)}</span>
                </div>
            </td>
            <td>${escapeHtml(book.author)}</td>
            <td><span class="badge ${isEbook ? 'badge-ebook' : 'badge-printed'}">${isEbook ? 'E-Book' : 'Printed'}</span></td>
            <td class="muted nowrap">${escapeHtml(book.extraInfo)}</td>
            <td class="nowrap"><strong>Rs. ${formatPrice(book.price)}</strong></td>
            <td>${book.quantity}</td>
            <td><span class="status ${status.cls}">${status.label}</span></td>
            <td>
                <div class="actions">
                    <a href="/book-form.html?id=${encodeURIComponent(book.id)}" class="icon-btn" title="Edit">${EDIT_ICON}</a>
                    <button type="button" class="icon-btn danger" data-id="${escapeHtml(book.id)}" title="Delete">${DELETE_ICON}</button>
                </div>
            </td>
        </tr>
    `;
}

function render() {
    const books = getFilteredBooks();

    if (books.length === 0) {
        tableWrap.style.display = 'none';
        tableFooter.style.display = 'none';
        emptyMessage.style.display = 'block';
        return;
    }

    tableWrap.style.display = 'block';
    tableFooter.style.display = 'flex';
    emptyMessage.style.display = 'none';

    const totalPages = Math.max(1, Math.ceil(books.length / PAGE_SIZE));
    currentPage = Math.min(currentPage, totalPages);

    const start = (currentPage - 1) * PAGE_SIZE;
    const pageBooks = books.slice(start, start + PAGE_SIZE);

    tableBody.innerHTML = pageBooks.map(rowHtml).join('');
    pageInfo.textContent = `Showing ${start + 1} to ${start + pageBooks.length} of ${books.length} entries`;
    renderPagination(totalPages);
}

function renderPagination(totalPages) {
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    let html = `<button class="page-btn" data-page="${currentPage - 1}" ${currentPage === 1 ? 'disabled' : ''}>‹</button>`;
    for (let i = 1; i <= totalPages; i++) {
        html += `<button class="page-btn ${i === currentPage ? 'active' : ''}" data-page="${i}">${i}</button>`;
    }
    html += `<button class="page-btn" data-page="${currentPage + 1}" ${currentPage === totalPages ? 'disabled' : ''}>›</button>`;
    pagination.innerHTML = html;
}

// ---------- Delete ----------

async function deleteBook(id) {
    if (!confirm('Are you sure you want to delete this book?')) return;

    try {
        const response = await fetch('/api/books/' + encodeURIComponent(id), { method: 'DELETE' });
        const data = await response.json();
        if (response.ok) {
            showAlert(data.message, 'success');
            loadBooks(false);
            loadTotals();
        } else {
            showAlert(data.error, 'error');
        }
    } catch (error) {
        showAlert('Could not delete book.', 'error');
    }
}

// ---------- Events ----------

// Delete button clicks
tableBody.addEventListener('click', event => {
    const button = event.target.closest('button[data-id]');
    if (button) deleteBook(button.dataset.id);
});

// Page number clicks
pagination.addEventListener('click', event => {
    const button = event.target.closest('button[data-page]');
    if (!button || button.disabled) return;
    currentPage = Number(button.dataset.page);
    render();
});

// Search (Enter key)
document.getElementById('searchForm').addEventListener('submit', event => {
    event.preventDefault();
    loadBooks();
});

// Live search while typing (waits 300 ms after the last key)
keywordInput.addEventListener('input', () => {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(() => loadBooks(), 300);
});

// Type filter (All / Printed / E-Book)
typeFilter.addEventListener('change', () => {
    currentPage = 1;
    render();
});

// Clear search and filter
document.getElementById('clearBtn').addEventListener('click', () => {
    keywordInput.value = '';
    typeFilter.value = 'ALL';
    loadBooks();
});

// Show success message after adding/editing (sent from the form page)
const params = new URLSearchParams(window.location.search);
if (params.get('msg')) {
    showAlert(params.get('msg'), 'success');
    window.history.replaceState({}, '', '/books.html');
}

loadTotals();
loadBooks();