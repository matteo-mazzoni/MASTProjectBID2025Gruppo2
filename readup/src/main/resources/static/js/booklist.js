document.addEventListener('DOMContentLoaded', function() {
    var confirmDeleteModal = document.getElementById('confirmDeleteModal');
    if (confirmDeleteModal) {
        confirmDeleteModal.addEventListener('show.bs.modal', function (event) {
            // Bottone che ha attivato la modal
            var button = event.relatedTarget;

            // Estrai le informazioni dagli attributi data-* del bottone
            var booklistId = button.getAttribute('data-booklist-id');
            var booklistName = button.getAttribute('data-booklist-name');

            // Aggiorna il testo nella modal con il nome della booklist
            var modalBodyName = confirmDeleteModal.querySelector('#booklistNameToDelete');
            if (modalBodyName) {
                modalBodyName.textContent = booklistName;
            }

            // Imposta l'azione del form di eliminazione all'interno della modal
            var deleteForm = confirmDeleteModal.querySelector('#deleteBooklistForm');
            if (deleteForm) {
                deleteForm.action = '/booklist/' + booklistId + '/delete'; // Assicurati che questo URL corrisponda al tuo controller
            }
        });
    }

    // Qui puoi mettere anche il tuo script esistente per riaprire la modal di creazione in caso di errore
    var errorMessage = /*[[${errorMessage}]]*/ null;
    if (errorMessage && errorMessage.includes("Errore nella creazione della booklist:")) {
        var myModal = new bootstrap.Modal(document.getElementById('createBooklistModal'));
        myModal.show();
    }
});