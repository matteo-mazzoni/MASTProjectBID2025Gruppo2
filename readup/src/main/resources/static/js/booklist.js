document.addEventListener('DOMContentLoaded', function() {
    var confirmDeleteModal = document.getElementById('confirmDeleteModal');
    if (confirmDeleteModal) {
        confirmDeleteModal.addEventListener('show.bs.modal', function (event) {
            // Button that triggered the modal
            var button = event.relatedTarget;

            // Extract information from data-* attributes
            var booklistId = button.getAttribute('data-booklist-id');
            var booklistName = button.getAttribute('data-booklist-name');

            // Update the modal content with the booklist name
            var modalBodyName = confirmDeleteModal.querySelector('#booklistNameToDelete');
            if (modalBodyName) {
                modalBodyName.textContent = booklistName;
            }

            // Set the action URL for the delete form
            var deleteForm = confirmDeleteModal.querySelector('#deleteBooklistForm');
            if (deleteForm) {
                deleteForm.action = '/booklist/' + booklistId + '/delete';
            }
        });
    }

    // Reopen the create modal in case of a booklist creation error
    var errorMessage = /*[[${errorMessage}]]*/ null;
    if (errorMessage && errorMessage.includes("Errore nella creazione della booklist:")) {
        var myModal = new bootstrap.Modal(document.getElementById('createBooklistModal'));
        myModal.show();
    }
});
