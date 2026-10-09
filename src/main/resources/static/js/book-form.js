const params = new URLSearchParams(window.location.search);
const bookId = params.get('id');          // null when adding a new book
const form = document.getElementById('bookForm');
const typeSelect = document.getElementById('type');
const coverInput = document.getElementById('cover');
const coverPreview = document.getElementById('coverPreview');
const coverPlaceholder = document.getElementById('coverPlaceholder');
const MAX_COVER_SIZE = 5 * 1024 * 1024;   // 5 MB

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

// Shows the cover preview (or the "No image" box)
function showCover(url) {
    if (url) {
        coverPreview.src = url;
        coverPreview.style.display = 'block';
        coverPlaceholder.style.display = 'none';
    } else {
        coverPreview.style.display = 'none';
        coverPlaceholder.style.display = 'grid';
    }
}

typeSelect.addEventListener('change', updateExtraLabel);

// Preview the chosen image before saving
coverInput.addEventListener('change', () => {
    const file = coverInput.files[0];
    if (!file) return;

    if (file.size > MAX_COVER_SIZE) {
        showError('Cover image must be 5 MB or smaller.');
        coverInput.value = '';
        return;
    }
    showCover(URL.createObjectURL(file));
});

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
        showCover(data.coverUrl);
        updateExtraLabel();
    } catch (error) {
        showError('Could not load book details.');
    }
}

// CREATE or UPDATE - send the form data (and image) to the backend
form.addEventListener('submit', async event => {
    event.preventDefault();

    const formData = new FormData();
    formData.append('type', typeSelect.value);
    formData.append('title', document.getElementById('title').value.trim());
    formData.append('author', document.getElementById('author').value.trim());
    formData.append('price', document.getElementById('price').value);
    formData.append('quantity', document.getElementById('quantity').value);
    formData.append('extra', document.getElementById('extra').value);

    if (coverInput.files[0]) {
        formData.append('cover', coverInput.files[0]);
    }

    const url = bookId ? '/api/books/' + encodeURIComponent(bookId) : '/api/books';

    try {
        // No Content-Type header: the browser sets it for file uploads
        const response = await fetch(url, { method: 'POST', body: formData });
        const data = await response.json();

        if (response.ok) {
            window.location.href = '/books.html?msg=' + encodeURIComponent(data.message);
        } else {
            showError(data.error || 'Please check the form and try again.');
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