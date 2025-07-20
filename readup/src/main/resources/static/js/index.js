/* Index Javascript - Logic for the main page of ReadUp */
/* Main contents: Carousel, Testimonial function, Buttons to add books on favourite page) */
/* Version: 1.0.0 */
/* Author: Romina Trazzi */


/*======================================================
        =            Carousel Selection            =
========================================================*/

// It selects the slides container
let slideContainer = document.getElementById("slides_container");

// It selects all the slides and create a Nodelist
let slideArray = document.querySelectorAll(".slide"); 

// It selects the arrow buttons
let leftArrow = document.getElementById("slide-arrow-prev");
let rightArrow = document.getElementById("slide-arrow-next");


/*===================================================
          =           Variables           =
====================================================*/

/* The index of current slide is 0 by default, while the max Index slide is the lenght of the Nodelist -1.
The number of all the slide is 0, 1, 2, 3 = 4. But the maximum index that slideArray can reach is 3 (last number).
Both the values of currentIndexSlide and maxIndexSlide refer to the index of the slideArray.*/

let currentIndexSlide = 0;
let maxIndexSlide = slideArray.length -1;  // 4 - 1 = 3



/*==================================================
          =            Function            =
====================================================*/

/* SCROLLLEFT PROPERTY allows to move an element horizontally (single slide) */

/* element.scrollLeft is a read-only property that returns the number of pixels that 
an element is scrolled horizontally starting from his left margin */

/* If slides container scrolleft is 0, the carousel is at the leftmost position and this mean 
that the it will start always from the first slide. */
slideContainer.scrollLeft = 0;

/* To move towards the next slide, we need to increase the scrollLeft value by the width of the slide when the 
arrow button is clicked (positive quantity, index from 0 to 3) */

rightArrow.addEventListener("click", () => {

  /* Now we have two cases:

  1. currentIndexSlide value is 3 (the maximum possible, it corresponds to the last slide and at the next click
  we will go back to the first slide)
  2. currentIndexSlide value is 0, 1, 2 (0 is the minimum possible, it corresponds to the first slide and at the next click
  we will go forward by one slide to the third slide) */
  
  // If currentIndexSlide value is 3 => set scrollleft property to 0 (= comes back to the 0 index slide - the first)
  if (currentIndexSlide === maxIndexSlide) {
    slideContainer.scrollLeft = 0; 
    currentIndexSlide = 0;
    
  /* If currentIndexSlide value is 0, 1, 2 => increase scrollleft property by the width of the slide */
  } else {
    
    // get the pixel width of the current slide
    const slideWidth = slideArray[currentIndexSlide].clientWidth; 

    // set scrollleft property to itself + the width of the current slide (it moves the slide conteiner by 1 slide forwards)
    slideContainer.scrollLeft += slideWidth; 

    // Increment the current slide index by 1
    currentIndexSlide++; 
  }
});

/* Repeat the same logic for the left arrow button, this time decreasing scrollLeft
(negative quantity, index goes from 3 down to 0) */

leftArrow.addEventListener("click", () => {

  /* Two cases:

  1. If currentIndexSlide value is 0 (the minimum possible, it corresponds to the first slide), at the next click we will move to the last slide (index 3)
  2. If currentIndexSlide is 1, 2, or 3, move one slide backward */
  
  // get the pixel width of the current slide
  const slideWidth = slideArray[currentIndexSlide].clientWidth;  

  // If the index of the slide container value is 0, move it to -1 slide and set current slide index to the maximum (3). 
  if (currentIndexSlide === 0) {

    // calculate total slide container width as: current slide width * total number of slides (so array index + 1, because their width is equal)
    totWidthContainer = slideWidth * (maxIndexSlide + 1);

    // set scrollLeft property to: total width of slides container - the width of the current slide (that is always the first)
    slideContainer.scrollLeft = totWidthContainer - slideWidth;  
    
    // set current slide index to the maximum (3)
    currentIndexSlide = maxIndexSlide; 
    
  /* If the current index slide value is 1, 2, 3 */
  } else {
    
    // set scrollLeft property to itself - the width of the current slide (it moves the slide container by 1 slide backwards)
    slideContainer.scrollLeft -= slideWidth; 

    // Decrease the current slide index by 1
    currentIndexSlide--; 
  }

});



/*==================================================
      =           Form validation             =
====================================================*/

// If fields are not valid, prevent the form submission
document.querySelector("form").addEventListener("submit", function (event) {
    if (!this.checkValidity()) {
      event.preventDefault();
    }
  });



/*==================================================
          =            Testimonials            =
====================================================*/

 const testimonials = [
    [4, "Andrea", "Cerrato", "ReadUp ha rivoluzionato il mio modo di leggere!"],
    [3, "Robert",  "Smau", "Bellissima community e suggerimenti sempre azzeccati!"],
    [4, "Giada",  "Perro",   "Grazie a ReadUp ho scoperto autori straordinari!"],
    [3, "Alessia",  "SanFilippo", "Interazione con altri lettori davvero stimolante!"],
    [4, "Elia", "Sollazzo",  "Ogni libro è diventato un’esperienza unica grazie a ReadUp."]
  ];


/*==================================================
      =           Testimonial Function           =
====================================================*/

const container = document.getElementById("sliderContent");
let idx = 0;

function renderTestimonials() {
  
  // Destructuring array
  const [stars, first, last, comment] = testimonials[idx];
  
  // Creating stars
  let starsHtml = `<span class="stars">`;
  for (let i = 0; i < stars; i++) starsHtml += "★";
  starsHtml += `</span>`;

  // Creating complete HTML node
  container.innerHTML =
    starsHtml +
    `<div class="comment">${comment}</div>` +
    `<div class="author">— ${first} ${last}</div>`;

  idx = (idx + 1) % testimonials.length;

 
}

renderTestimonials();
setInterval(renderTestimonials, 20000);

    
/*==================================================
=           Button (Aggiungi ai miei libri)          =
====================================================*/
document.addEventListener('DOMContentLoaded', () => {
  // 1) Seleziono tutti i bottoni "add-book-btn"
  const addBtns = document.querySelectorAll('.add-book-btn');
  // 2) Se non ne trovo, esco subito (nessun errore)
  if (!addBtns || addBtns.length === 0) {
    return;
  }

  // 3) Prendo meta CSRF (opzionale, solo se hai CSRF enabled)
  const csrfMeta    = document.querySelector('meta[name="_csrf"]');
  const csrfHeader  = document.querySelector('meta[name="_csrf_header"]');
  const csrfToken   = csrfMeta    ? csrfMeta.content    : null;
  const csrfHeaderName = csrfHeader ? csrfHeader.content : null;

  // 4) Aggiungo event listener a ciascun bottone
  addBtns.forEach(btn => {
    btn.addEventListener('click', event => {
      event.preventDefault();
      
      const bookId = btn.getAttribute('data-book-id');
      if (!bookId) {
        console.warn('Bottone senza data-book-id!');
        return;
      }

      const headers = { 'Content-Type': 'application/json' };
      if (csrfHeaderName && csrfToken) {
        headers[csrfHeaderName] = csrfToken;
      }

      fetch('/api/user/books/add', {
        method: 'POST',
        headers,
        body: JSON.stringify({ bookId: Number(bookId) })
      })
      .then(resp => {
        if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
        return resp.json();
      })
      .then(data => {
        if (data.success) {
          alert('Libro aggiunto con successo!');
          window.location.href = '/libri'
        } else {
          alert('Errore: ' + (data.message || 'Impossibile aggiungere'));
        }
      })
      .catch(err => {
        console.error('Fetch error:', err);
        alert('Qualcosa è andato storto.');
      });
    });
  });
});

