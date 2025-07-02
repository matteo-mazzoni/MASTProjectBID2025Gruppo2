
/* This script is used to filter books by title, author, and genre */


document.addEventListener('DOMContentLoaded', function() {
  
    /*======================================================
        =            Input  and Card Selection            =
    ========================================================*/
 
    const inputTitle  = document.getElementById('titleFilter');
    const inputAuthor = document.getElementById('authorFilter');
    const selectGenre = document.getElementById('genreFilter');

    const cards = document.querySelectorAll('.book-card');

    /*======================================================
                    =            Filters        =
    ========================================================*/
 
    function filterBooks() {
        const titleVal  = inputTitle.value.trim().toLowerCase();
        const authorVal = inputAuthor.value.trim().toLowerCase();
        const genreVal  = selectGenre.value; 

        cards.forEach(card => {
            const t = card.getAttribute('data-title');
            const a = card.getAttribute('data-author');
            const g = card.getAttribute('data-genre');

    
    /*======================================================
                =           Matching values:        =
    ========================================================*/
 
            const matchTitle  = !titleVal  || t.includes(titleVal);
            const matchAuthor = !authorVal || a.includes(authorVal);
            const matchGenre  = !genreVal  || g === genreVal;

            // Display the results based on the filters, otherwise hide the card
            card.style.display = (matchTitle && matchAuthor && matchGenre) ? '' : 'none';
        });
    }


    /*======================================================
            =           Add event listeners       =
    ========================================================*/
    
    inputTitle.addEventListener('input',  filterBooks);
    inputAuthor.addEventListener('input', filterBooks);
    selectGenre.addEventListener('change', filterBooks);
});


/* This script is used to toggle the visibility of the book description */

 document.addEventListener('DOMContentLoaded', () => {

    // find all clickable elements inside cards
    document.querySelectorAll('.card .clickable').forEach(el => {
        el.addEventListener('click', () => {
            const card = el.closest('.card');
            if (!card) return;
            // description div id inside this card
            const desc = card.querySelector('[id^="desc-"]');
            if (!desc) return;
            // toggle visibility
            desc.style.display = (desc.style.display === 'none' || desc.style.display === '') ? 'block' : 'none';
        });
    });
});
