document.addEventListener('DOMContentLoaded', function() {
    const searchBookForm = document.getElementById('searchBookForm');
    const bookSearchQuery = document.getElementById('bookSearchQuery');
    const searchResults = document.getElementById('searchResults');
    const noResultsMessage = document.getElementById('noResultsMessage');
    const addBookModalElement = document.getElementById('addBookModal');
    const addBookModal = new bootstrap.Modal(addBookModalElement);
    const alertContainer = document.getElementById('alertContainer'); // Contenitore per i messaggi dinamici

    // Recupera i dati passati dal backend tramite data-attributes del body
    const booklistId = document.body.dataset.booklistId;
    const initialErrorMessage = document.body.dataset.errorMessage;
    const initialSuccessMessage = document.body.dataset.successMessage;

    // Funzione per mostrare/nascondere il messaggio "Nessun risultato"
    function toggleNoResultsMessage(show) {
        noResultsMessage.style.display = show ? 'block' : 'none';
    }

    // Funzione per mostrare un messaggio di allerta
    function showAlert(message, type) {
        const alertHtml = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                <span>${message}</span>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        // Pulisce messaggi precedenti dello stesso tipo se vuoi mostrare solo l'ultimo
        // alertContainer.innerHTML = ''; 
        alertContainer.insertAdjacentHTML('afterbegin', alertHtml);
    }

    // Inizialmente mostra il messaggio "Nessun libro trovato"
    toggleNoResultsMessage(true);

    // Gestione dell'invio del form di ricerca libri
    searchBookForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Impedisce il submit del form standard
        const query = bookSearchQuery.value.trim();

        if (query.length < 2) { // Evita ricerche troppo brevi
            searchResults.innerHTML = '';
            toggleNoResultsMessage(true);
            return;
        }

        searchResults.innerHTML = ''; // Pulisce i risultati precedenti
        toggleNoResultsMessage(false); // Nasconde il messaggio "Nessun libro trovato"

        // Esegui la ricerca via API
        fetch('/api/libri/search?query=' + encodeURIComponent(query))
            .then(response => {
                if (!response.ok) {
                    throw new Error('Errore di rete o server');
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
                    // Allega l'event listener ai form di aggiunta libro appena creati
                    document.querySelectorAll('.add-libro-form').forEach(form => {
                        form.addEventListener('submit', handleAddBookFormSubmit);
                    });
                } else {
                    toggleNoResultsMessage(true); // Mostra il messaggio se nessun risultato
                }
            })
            .catch(error => {
                console.error('Errore durante la ricerca dei libri:', error);
                searchResults.innerHTML = '<div class="alert alert-danger mt-3">Errore durante la ricerca. Riprova più tardi.</div>';
                toggleNoResultsMessage(false); // Nasconde il messaggio di nessun risultato in caso di errore
            });
    });

    // Gestione dell'invio del form di aggiunta libro (via AJAX)
    function handleAddBookFormSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const libroId = form.dataset.libroId; // Recupera l'ID del libro dal data-attribute del form

        // Recupera il token CSRF di Spring Security per le richieste POST
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        fetch(`/booklist/${booklistId}/add-libro`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [csrfHeader]: csrfToken // Aggiunge il token CSRF
            },
            body: `libroId=${encodeURIComponent(libroId)}` // Invia l'ID del libro come parametro del form
        })
        .then(response => {
            if (!response.ok) {
                // Se il backend risponde con un errore HTTP (es. 400, 403)
                return response.text().then(text => { throw new Error(text || 'Errore sconosciuto'); });
            }
            // Se tutto va bene, il backend dovrebbe reindirizzare o inviare un messaggio JSON di successo
            // Per semplicità con l'attuale backend che reindirizza, ricarichiamo la pagina
            window.location.reload(); 
        })
        .catch(error => {
            console.error('Errore durante l\'aggiunta del libro:', error);
            // Visualizza un messaggio di errore nella modal o nella pagina
            // Qui assumiamo che il messaggio dell'errore contenga il motivo (es. "Libro già presente")
            const errorMessageText = error.message.includes('Libro già presente') ? 
                                     'Il libro è già presente in questa booklist.' : 
                                     'Si è verificato un errore durante l\'aggiunta del libro.';
            showAlert(errorMessageText, 'danger');
            addBookModal.show(); // Ri-apre la modal per permettere all'utente di correggere
        });
    }

    // Logica per ri-aprire la modal e mostrare messaggi all'avvio della pagina (dopo un redirect)
    if (initialErrorMessage) {
        if (initialErrorMessage.includes('Libro già presente') || initialErrorMessage.includes('non trovato con ID')) {
            addBookModal.show(); // Ri-apre la modal se l'errore è dovuto a libro già presente o ID non trovato
        }
    }
    
    // Puoi anche mostrare initialSuccessMessage se vuoi che appaia anche dopo un ricarica
    if (initialSuccessMessage) {
        showAlert(initialSuccessMessage, 'success');
    }
});