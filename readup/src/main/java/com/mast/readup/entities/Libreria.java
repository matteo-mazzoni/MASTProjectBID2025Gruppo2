package com.mast.readup.entities;
import java.util.Objects;

import jakarta.persistence.*;

// These annotations indicates that this class is an entity and will be mapped to a database table called "libreria".
@Entity
@Table(name = "libreria")
public class Libreria {

    // Link to the composite key class LibreriaKey
    @EmbeddedId
    private LibreriaKey id;

    // Add the extra field of the entity
    @Column(name = "stato_prestito")
    private String statoPrestito;

    // Navigation properties: these properties are used to establish the relationship between Libreria entity and Libro and Utente entities.
    @MapsId("idLibro")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @MapsId("idUtente")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente")
    private Utente utente;

    // Basic constructor 
    public Libreria() {}

    // Constructor with parameters
    public Libreria(LibreriaKey id, String statoPrestito) {
        this.id = id;
        this.statoPrestito = statoPrestito;
    }

    // Getter / Setter
    public LibreriaKey getId() 
    { 
        return id; 
    }
    public void setId(LibreriaKey id) 
    { 
        this.id = id; 
    }

    public String getStatoPrestito() { 
        return statoPrestito; 
    }
    public void setStatoPrestito(String statoPrestito) 
    { 
        this.statoPrestito = statoPrestito; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Libreria)) return false;
        Libreria that = (Libreria) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}