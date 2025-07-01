document.addEventListener('DOMContentLoaded', function() {
    const searchBookForm = document.getElementById('searchBookForm');
    const bookSearchQuery = document.getElementById('bookSearchQuery');
    const searchResults = document.getElementById('searchResults');
    const noResultsMessage = document.getElementById('noResultsMessage');
    const addBookModalElement = document.getElementById('addBookModal');
    const addBookModal = new bootstrap.Modal(addBookModalElement);
    const alertContainer = document.getElementById('alertContainer');

    // Get data from body attributes injected by the backend
    const booklistId = document.body.dataset.booklistId;
    const initialErrorMessage = document.body.dataset.errorMessage;
    const initialSuccessMessage = document.body.dataset.successMessage;

    // Show/hide "no results" message
    function toggleNoResultsMessage(show) {
        noResultsMessage.style.display = show ? 'block' : 'none';
    }

    // Display alert message in the alert container
    function showAlert(message, type) {
        const alertHtml = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                <span>${message}</span>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        alertContainer.insertAdjacentHTML('afterbegin', alertHtml);
    }

    // Show default "no books found" message
    toggleNoResultsMessage(true);

    // Handle book search form submission
    searchBookForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const query = bookSearchQuery.value.trim();

        if (query.length < 2) {
            searchResults.innerHTML = '';
            toggleNoResultsMessage(true);
            return;
        }

        searchResults.innerHTML = '';
        toggleNoResultsMessage(false);

        // Perform search via API
        fetch('/api/libri/search?query=' + encodeURIComponent(query))
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network or server error');
                }
                return response.json();
            })
            .then(data => {
                if (data.length > 0) {
                    data.forEach(libro => {
                        const listItem = document.createElement('div');
                        listItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
                        listItem.innerHTML = `
                            <div>
                                <h6 class="mb-1">${libro.titolo}</h6>
                                <small class="text-muted">${libro.autore}</small>
                            </div>
                            <form class="add-libro-form" data-libro-id="${libro.idLibro}">
                                <button type="submit" class="btn btn-sm btn-primary">Aggiungi</button>
                            </form>
                        `;
                        searchResults.appendChild(listItem);
                    });

                    // Attach event listener to each add-book form
                    document.querySelectorAll('.add-libro-form').forEach(form => {
                        form.addEventListener('submit', handleAddBookFormSubmit);
                    });
                } else {
                    toggleNoResultsMessage(true);
                }
            })
            .catch(error => {
                console.error('Error while searching books:', error);
                searchResults.innerHTML = '<div class="alert alert-danger mt-3">Search error. Please try again later.</div>';
                toggleNoResultsMessage(false);
            });
    });

    // Handle book add form submission via AJAX
    function handleAddBookFormSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const libroId = form.dataset.libroId;

        // Get Spring Security CSRF token and header
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        fetch(`/booklist/${booklistId}/add-libro`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [csrfHeader]: csrfToken
            },
            body: `libroId=${encodeURIComponent(libroId)}`
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text || 'Unknown error'); });
            }
            window.location.reload();
        })
        .catch(error => {
            console.error('Error while adding book:', error);
            const errorMessageText = error.message.includes('Libro già presente') ?
                                     'This book is already in the booklist.' :
                                     'An error occurred while adding the book.';
            showAlert(errorMessageText, 'danger');
            addBookModal.show();
        });
    }

    // Reopen modal if redirect included error message
    if (initialErrorMessage) {
        if (initialErrorMessage.includes('Libro già presente') || initialErrorMessage.includes('non trovato con ID')) {
            addBookModal.show();
        }
    }

    // Show success message on page load if available
    if (initialSuccessMessage) {
        showAlert(initialSuccessMessage, 'success');
    }
});
