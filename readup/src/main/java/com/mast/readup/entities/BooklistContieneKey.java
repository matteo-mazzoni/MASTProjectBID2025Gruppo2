package com.mast.readup.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable

// This annotation indicates that this class represent a composite key for an entity (BooklistContiene).
public class BooklistContieneKey implements Serializable {
    
    // Foreign keys for the composite key
    private Long idLibro;   //  Foreign Key towards Libro
    private Long idBooklist; // Foreign Key towards Booklist
    private Long idUtente; // Foreign Key towards Utente

    // Basic constructor
    public BooklistContieneKey() {}

    // Constructor with parameters
    public BooklistContieneKey(Long idLibro, Long idBooklist, Long idUtente) {
        this.idLibro = idLibro; 
        this.idBooklist = idBooklist; 
        this.idUtente = idUtente; 
    }


    // Getter / Setter
    public Long getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Long idLibro) {
        this.idLibro = idLibro;
    }

    public Long getIdBooklist() {
        return idBooklist;
    }

    public void setIdBooklist(Long idBooklist) {
        this.idBooklist = idBooklist;
    }

    public Long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Long idUtente) {
        this.idUtente = idUtente;
    }

    // Overrides equals(Object) to compare BooklistContieneKey instances by idLibro, idBooklist, and idUtente for logical equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooklistContieneKey)) return false;
        BooklistContieneKey that = (BooklistContieneKey) o;
        return Objects.equals(idLibro, that.idLibro) &&
               Objects.equals(idBooklist, that.idBooklist) &&
               Objects.equals(idUtente, that.idUtente);
    }

    // Overrides hashCode() to generate a consistent hash based on idLibro, idBooklist, and idUtente, ensuring equal keys yield the same hash
    @Override
    public int hashCode() {
        return Objects.hash(idLibro, idBooklist, idUtente);
    }
}