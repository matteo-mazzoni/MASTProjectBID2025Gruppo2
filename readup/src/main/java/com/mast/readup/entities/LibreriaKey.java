package com.mast.readup.entities;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


// This annotation indicates that this class represent a composite key for an entity (Libreria).
@Embeddable                      
public class LibreriaKey implements Serializable {    

    // Foreign keys for the composite key
    private Long idLibro;         //  Foreign Key towards Libro
    private Long idUtente;        // Foreign Key towards Utente
    
    
    // Basic constructor
    public LibreriaKey() {}


    // Constructor with parameters
    public LibreriaKey(Long idLibro, Long idUtente) {
        this.idLibro = idLibro;
        this.idUtente = idUtente;
    }
    
    // Getter / Setter
    public Long getIdLibro() { 
        return idLibro; 
    }

    public void setIdLibro(Long idLibro) 
    { 
        this.idLibro = idLibro; 
    }
    public Long getIdUtente() 
    { 
        return idUtente; 
    }
    public void setIdUtente(Long idUtente) 
    { 
        this.idUtente = idUtente; 
    }


    // Overrides equals(Object) to compare LibreriaKey instances by idLibro and idUtente for logical equality
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LibreriaKey other = (LibreriaKey) obj;
        return Objects.equals(idLibro, other.idLibro) &&
               Objects.equals(idUtente, other.idUtente);
    }

    // Overrides hashCode() to generate a consistent hash based on idLibro and idUtente, ensuring equal keys yield the same hash
    @Override
    public int hashCode() {
        return Objects.hash(idLibro, idUtente);
    }
}