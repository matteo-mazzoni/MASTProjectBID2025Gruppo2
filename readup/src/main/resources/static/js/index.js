/*======================================================
        =            Carousel Selection            =
========================================================*/

// Selezionare il container delle slide
let slideContainer = document.getElementById("slides_container");

//  Selezionare le slide 
let slideArray = document.querySelectorAll(".slide"); // Crea una NodeList

// Selezionare gli arrow button
let leftArrow = document.getElementById("slide-arrow-prev");
let rightArrow = document.getElementById("slide-arrow-next");


/*===================================================
            =           Variables           =
====================================================*/

// Impostiamo un valore indice per la slide corrente e il numero massimo di slide
let currentIndexSlide = 0;
let maxIndexSlide = slideArray.length -1;  // 4 - 1 = 3

/* le slide sono 0 1 2 3, cioè 4. Il valore massimo che l'indice dello slideArray però può raggiungere è 3. 
Sia il valore di currentIndexSlide che il valore di maxIndexSlide si riferiscono all'indice dell'array slideArray*/


/*==================================================
          =            Function            =
====================================================*/

/* UTILIZZIAMO LA PROPRIETA' SCROLLLEFT PER SPOSTARE LO SLIDER. */

/* element.scrollLeft imposta il numero di pixel di spostamento orizzontale di un elemento 
prendendo come punto di riferimento iniziale il suo margine sinistro. */

// Impostiamo la proprietà scrollLeft a 0 per far iniziare il carosello sempre dalla prima slide
slideContainer.scrollLeft = 0;


/* Poiché desideriamo visualizzare la slide seguente quando viene cliccato l'arrow button dx, dobbiamo spostare
il contenitore delle slide di un numero di pixel uguale alla larghezza di una singola slide (q.tà positiva, indice da 0 => 3). */

rightArrow.addEventListener("click", () => {

  /* Due casi: 
  
  1. currentIndexSlide vale 3 (cioè il massimo possibile, ULTIMA SLIDE), bisogna tornare alla slide 0
  2. currentIndexSlide vale 0, 1, 2 e bisogna andare avanti di una slide */ 

  /* Se current index slide vale 3 => imposta la proprietà scrollLeft a 0 (= torna alla slide 0) e l'indice della slide corrente a 0 */
  if (currentIndexSlide === maxIndexSlide) {
    slideContainer.scrollLeft = 0; 
    currentIndexSlide = 0;
    
  /* Se current index slide vale 0, 1, 2 */
  } else {
    
    // n. di pixel pari alla larghezza della slide corrente
    const slideWidth = slideArray[currentIndexSlide].clientWidth; 

    // imposta la proprietà scrollLeft pari a sé stessa + la larghezza della slide corrente (= sposta di +1 il contenitore slide)
    slideContainer.scrollLeft += slideWidth; 

    // aumenta di 1 il valore dell'indice della slide corrente 
    currentIndexSlide++; 
  }

  console.log("Current Index Slide:", currentIndexSlide);
  console.log("Scroll Left Value:", slideContainer.scrollLeft);

});

/* Ripetiamo col pulsante sinistro, dimimnuendo lo scrollleft (q.tà negativa, indice da 3 => 0) */

leftArrow.addEventListener("click", () => {

  /* Due casi: 
  
  1. currentIndexSlide vale 0 (cioè il minimo possibile, PRIMA SLIDE), bisogna tornare all'ultima slide con indice 3
  2. currentIndexSlide vale 1, 2, 3 e bisogna andare indietro di una slide */ 
  
  // n. di pixel pari alla larghezza della slide corrente
  const slideWidth = slideArray[currentIndexSlide].clientWidth;  

  /* Se current index slide vale 0, sposta il contenitore delle slide di -1 slide per visualizzare l'ultima slide 
  e imposta l'indice della slide corrente uguale a quello massimo */
  if (currentIndexSlide === 0) {

    // totale larghezza contenitore slide: singola larghezza corrente * numero totale delle slide (quindi indice array + 1)
    totWidthContainer = slideWidth * (maxIndexSlide + 1);

    // imposta la proprietà scrolleft a: totale larghezza contenitore - larghezza slide corrente (che è sempre la prima)
    slideContainer.scrollLeft = totWidthContainer - slideWidth;  
    
    // imposta l'indice della slide corrente all'ultima slide, ovvero 3
    currentIndexSlide = maxIndexSlide; 
    
  /* Se current index slide vale 1, 2, 3 */
  } else {
    
    // imposta la proprietà scrollLeft pari a sé stessa - la larghezza della slide corrente (= sposta di -1 il contenitore slide)
    slideContainer.scrollLeft -= slideWidth; 

    // diminuisci di 1 il valore dell'indice della slide corrente
    currentIndexSlide--; 
  }

  console.log("Current Index Slide:", currentIndexSlide);
  console.log("Scroll Left Value:", slideContainer.scrollLeft);

});


/* UTILIZZIAMO UN EVENT LISTENER CHE OSSERVA IL RESIZE DELLA FINESTRA DEL BROWSER TRAMITE LA FUNZIONE resizeHandler */

window.addEventListener('resize', resizeHandler);

const resizeHandler = () => {

  // Aggiorna la larghezza delle slide
  slideWidth = slideArray[currentIndexSlide].clientWidth;

  /* Aggiorna la posizione del contenitore delle slide in base alla nuova larghezza

  Due casi:

  1. Se current index slide vale 0, lo scrollLeft rimane sempre pari a 0
  2. Se current index slide vale 1, 2, 3, lo scrolleft è pari all'indice della slide corrente per la larghezza della singola slide corrente */

  if (currentIndexSlide === 0) {
    slideContainer.scrollLeft = 0;
  } else {
    slideContainer.scrollLeft = currentIndexSlide * slideWidth;
  }
}

/*=====  End of Function  ======*/


/*==================================================
          =            Testimonnials            =
====================================================*/

 const testimonials = [
    [4, "Mario", "Rossi", "ReadUp ha rivoluzionato il mio modo di leggere!"],
    [3, "Anna",  "Bianchi", "Bellissima community e suggerimenti sempre azzeccati!"],
    [4, "Luca",  "Verdi",   "Grazie a ReadUp ho scoperto autori straordinari!"],
    [3, "Sara",  "Neri",    "Interazione con altri lettori davvero stimolante!"],
    [4, "Paolo", "Galli",  "Ogni libro è diventato un’esperienza unica grazie a ReadUp."]
  ];


/*==================================================
          =            Functions            =
====================================================*/

  function renderTestimonials() {
    const container = document.getElementById("testimonialsContainer");
    testimonials.forEach(t => {
      const [stars, first, last, comment] = t;

      // Crea la colonna
      const col = document.createElement("div");
      col.className = "col-md-6 col-lg-4";

      // Crea la card
      const card = document.createElement("div");
      card.className = "card h-100 shadow-sm p-4";

      // Stelle
      const starsDiv = document.createElement("div");
      starsDiv.className = "mb-2";
      for (let i = 1; i <= 4; i++) {
        const span = document.createElement("span");
        span.className = "fa fa-star" + (i <= stars ? " checked" : "");
        starsDiv.appendChild(span);
      }
      card.appendChild(starsDiv);

      // Testo recensione
      const p = document.createElement("p");
      p.className = "mb-3";
      p.textContent = comment;
      card.appendChild(p);

      // Nome e cognome
      const h6 = document.createElement("h6");
      h6.className = "mb-0";
      h6.textContent = `${first} ${last}`;
      card.appendChild(h6);

      // Monta struttura
      col.appendChild(card);
      container.appendChild(col);
    });
  }

  // 3) Avvia il rendering dopo che il DOM è pronto
  document.addEventListener("DOMContentLoaded", renderTestimonials);
