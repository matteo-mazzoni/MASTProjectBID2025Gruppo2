/* Index Javascript - Logic for the user's book page */
/* Main contents: Check and Remove books for user's favourite list  */
/* Version: 1.0.0 */
/* Author: Romina Trazzi */

s
document.addEventListener('DOMContentLoaded', () => {

const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');   

    // Managing checkboxes for 'Letto' status
  document.querySelectorAll('.letto-checkbox').forEach(cb => {
    cb.addEventListener('change', () => {
      const bookId = cb.dataset.bookId;
      fetch('/user/books/updateLetto', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          [csrfHeader]: csrfToken
        },
        body: JSON.stringify({ bookId, letto: cb.checked })
      })
      .then(r => r.json())
      .then(data => {
        if (!data.success) alert('Errore nel salvataggio');
      });
    });
  });

  // Managing the removal of books
  document.querySelectorAll('.remove-book-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const bookId = btn.dataset.bookId;
      fetch('/remove', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          [csrfHeader]: csrfToken
        },
        body: JSON.stringify({ bookId })
      })
      .then(r => r.json())
      .then(data => {
        if (data.success) {
          // Rimuovi la riga dalla tabella
          btn.closest('tr').remove();
        } else {
          alert('Errore nella rimozione');
        }
      });
    });
  });
});