package com.mast.readup.entities;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.Objects;

// These annotations indicates that this class is an entity and will be mapped to a database table called "booklist_contiene".
@Entity
@Table(name = "booklist_contiene")
public class BooklistContiene {

    // Link to the composite key class BooklistContieneKey
    @EmbeddedId
    private BooklistContieneKey id;

    // Navigation properties: these properties are used to establish the relationship between BooklistContiene entity and Libro, Booklist, and Utente entities.
    @MapsId("idLibro")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @MapsId("idBooklist")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_booklist")
    private Booklist booklist;

    @MapsId("idUtente")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente")
    private Utente utente;

    // Basic constructor
    public BooklistContiene() {}

    // Constructor with parameters
    public BooklistContiene(BooklistContieneKey id) {
        this.id = id;
    }

    // Getter / Setter
    public BooklistContieneKey getId() {
        return id;
    }

    public void setId(BooklistContieneKey id) {
        this.id = id;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Booklist getBooklist() {
        return booklist;
    }

    public void setBooklist(Booklist booklist) {
        this.booklist = booklist;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    // Overrides equals(Object) to compare BooklistContiene instances by id for logical equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooklistContiene)) return false;
        BooklistContiene that = (BooklistContiene) o;
        return Objects.equals(id, that.id);
    }

    // Overrides hashCode() to generate a consistent hash based on id, ensuring equal entities yield the same hash
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}