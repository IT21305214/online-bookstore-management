// Loads the dashboard numbers from the backend
async function loadStats() {
    try {
        const response = await fetch('/api/books/stats');
        const stats = await response.json();
        document.getElementById('totalBooks').textContent = stats.totalBooks;
        document.getElementById('totalStock').textContent = stats.totalStock;
        document.getElementById('ebookCount').textContent = stats.ebookCount;
    } catch (error) {
        console.error('Could not load stats', error);
    }
}

loadStats();