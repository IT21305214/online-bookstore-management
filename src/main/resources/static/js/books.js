const table = document.getElementById('bookTable');
const tableBody = document.getElementById('bookTableBody');
const emptyMessage = document.getElementById('emptyMessage');
const keywordInput = document.getElementById('keyword');

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

function showAlert(message, type) {
    const alertBox = document.getElementById('alert');
    alertBox.textContent = message;
    alertBox.className = 'alert alert-' + type;
    alertBox.style.display = 'block';
    setTimeout(() => alertBox.style.display = 'none', 4000);
}

// READ - get books from the backend (optionally filtered by keyword)
async function loadBooks(keyword = '') {
    try {
        const url = keyword
            ? '/api/books?keyword=' + encodeURIComponent(keyword)
            : '/api/books';
        const response = await fetch(url);
        const books = await response.json();
        renderBooks(books);
    } catch (error) {
        showAlert('Could not load books. Is the server running?', 'error');
    }
}

// Builds the table rows
function renderBooks(books) {
    if (books.length === 0) {
        table.style.display = 'none';
        emptyMessage.style.display = 'block';
        return;
    }

    table.style.display = 'table';
    emptyMessage.style.display = 'none';

    tableBody.innerHTML = books.map(book => `
        <tr>
            <td>${escapeHtml(book.id)}</td>
            <td>${escapeHtml(book.title)}</td>
            <td>${escapeHtml(book.author)}</td>
            <td>
                <span class="badge ${book.type === 'EBOOK' ? 'ebook' : ''}">
                    ${book.type === 'EBOOK' ? 'E-Book' : 'Printed'}
                </span>
            </td>
            <td>${escapeHtml(book.extraInfo)}</td>
            <td>${formatPrice(book.price)}</td>
            <td>${formatPrice(book.finalPrice)}</td>
            <td>${book.quantity}</td>
            <td class="actions">
                <a href="/book-form.html?id=${encodeURIComponent(book.id)}" class="btn btn-sm">Edit</a>
                <button type="button" class="btn btn-sm btn-danger" data-id="${escapeHtml(book.id)}">Delete</button>
            </td>
        </tr>
    `).join('');
}

// DELETE - remove a book
async function deleteBook(id) {
    if (!confirm('Are you sure you want to delete this book?')) return;

    try {
        const response = await fetch('/api/books/' + encodeURIComponent(id), { method: 'DELETE' });
        const data = await response.json();
        if (response.ok) {
            showAlert(data.message, 'success');
            loadBooks(keywordInput.value.trim());
        } else {
            showAlert(data.error, 'error');
        }
    } catch (error) {
        showAlert('Could not delete book.', 'error');
    }
}

// Delete button clicks
tableBody.addEventListener('click', event => {
    const button = event.target.closest('button[data-id]');
    if (button) deleteBook(button.dataset.id);
});

// Search
document.getElementById('searchForm').addEventListener('submit', event => {
    event.preventDefault();
    loadBooks(keywordInput.value.trim());
});

// Clear search
document.getElementById('clearBtn').addEventListener('click', () => {
    keywordInput.value = '';
    loadBooks();
});

// Show success message after adding/editing (sent from the form page)
const params = new URLSearchParams(window.location.search);
if (params.get('msg')) {
    showAlert(params.get('msg'), 'success');
    window.history.replaceState({}, '', '/books.html');
}

loadBooks();