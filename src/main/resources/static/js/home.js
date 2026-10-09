const BOOK_ICON = '<svg class="icon" viewBox="0 0 24 24"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>';

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatPrice(value) {
    return Number(value).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// Loads all books once and builds the dashboard from them
async function loadDashboard() {
    try {
        const response = await fetch('/api/books');
        const books = await response.json();

        const ebooks = books.filter(book => book.type === 'EBOOK').length;
        document.getElementById('totalBooks').textContent = books.length;
        document.getElementById('totalStock').textContent =
            books.reduce((sum, book) => sum + book.quantity, 0);
        document.getElementById('ebookCount').textContent = ebooks;
        document.getElementById('printedCount').textContent = books.length - ebooks;

        // Last 5 books added (newest first)
        const recent = books.slice(-5).reverse();
        if (recent.length === 0) {
            document.getElementById('recentWrap').style.display = 'none';
            document.getElementById('recentEmpty').style.display = 'block';
            return;
        }

        document.getElementById('recentBody').innerHTML = recent.map(book => `
            <tr>
                <td>
                    <div class="book-cell">
                        ${book.coverUrl
                            ? `<img class="cover" src="${escapeHtml(book.coverUrl)}" alt="">`
                            : `<div class="cover cover-empty">${BOOK_ICON}</div>`}
                        <span class="book-title">${escapeHtml(book.title)}</span>
                    </div>
                </td>
                <td>${escapeHtml(book.author)}</td>
                <td>
                    <span class="badge ${book.type === 'EBOOK' ? 'badge-ebook' : 'badge-printed'}">
                        ${book.type === 'EBOOK' ? 'E-Book' : 'Printed'}
                    </span>
                </td>
                <td class="nowrap"><strong>Rs. ${formatPrice(book.price)}</strong></td>
                <td>${book.quantity}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Could not load dashboard', error);
    }
}

loadDashboard();