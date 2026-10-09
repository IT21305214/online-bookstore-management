const params = new URLSearchParams(window.location.search);
const bookId = params.get('id');          // null when adding a new book
const form = document.getElementById('bookForm');
const typeSelect = document.getElementById('type');

// Changes the last field's label depending on the book type
function updateExtraLabel() {
    document.getElementById('extraLabel').textContent =
        typeSelect.value === 'EBOOK' ? 'File Size (MB)' : 'Number of Pages';
}

function showError(message) {
    const alertBox = document.getElementById('alert');
    alertBox.textContent = message;
    alertBox.style.display = 'block';
}

typeSelect.addEventListener('change', updateExtraLabel);

// UPDATE mode - fill the form with the existing book
async function loadBookForEdit() {
    document.getElementById('pageTitle').textContent = 'Edit Book';
    document.title = 'BookNest | Edit Book';

    try {
        const response = await fetch('/api/books/' + encodeURIComponent(bookId));
        const data = await response.json();

        if (!response.ok) {
            showError(data.error);
            return;
        }

        typeSelect.value = data.type;
        document.getElementById('title').value = data.title;
        document.getElementById('author').value = data.author;
        document.getElementById('price').value = data.price;
        document.getElementById('quantity').value = data.quantity;
        document.getElementById('extra').value = data.extraValue;
        updateExtraLabel();
    } catch (error) {
        showError('Could not load book details.');
    }
}

// CREATE or UPDATE - send the form data to the backend
form.addEventListener('submit', async event => {
    event.preventDefault();

    const book = {
        type: typeSelect.value,
        title: document.getElementById('title').value.trim(),
        author: document.getElementById('author').value.trim(),
        price: parseFloat(document.getElementById('price').value),
        quantity: parseInt(document.getElementById('quantity').value),
        extra: parseFloat(document.getElementById('extra').value)
    };

    const url = bookId ? '/api/books/' + encodeURIComponent(bookId) : '/api/books';
    const method = bookId ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(book)
        });
        const data = await response.json();

        if (response.ok) {
            window.location.href = '/books.html?msg=' + encodeURIComponent(data.message);
        } else {
            showError(data.error);
        }
    } catch (error) {
        showError('Could not save book. Is the server running?');
    }
});

if (bookId) {
    loadBookForEdit();
} else {
    updateExtraLabel();
}