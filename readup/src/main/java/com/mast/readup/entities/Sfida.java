package com.mast.readup.entities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable; // Importa JoinTable
import jakarta.persistence.ManyToMany; // Importa ManyToMany
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet; // Importa HashSet
import java.util.List;
import java.util.Set; // Importa Set

@Entity
@Table(name = "sfida")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sfida implements JsonStringListConverter { // Considera se JsonStringListConverter è ancora necessario

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codice_sfida")
    @EqualsAndHashCode.Include // Includi codiceSfida per equals/hashCode
    private long codiceSfida;

    @Column(name = "nome_sfida", length = 80) // Rimuovi nullable=true se non strettamente necessario, default è true
    private String nomeSfida;

    @Column(name = "descrizione_sfida", length = 500)
    private String descrizioneSfida;

    @Column(name = "num_partecipanti", nullable = false)
    @NotNull
    private int numPartecipanti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente_creatore", nullable = false)
    @NotNull
    private Utente utenteCreatore;

    @Column(name = "data_inizio", nullable = false)
    @NotNull
    private LocalDate dataInizio;

    @Column(name = "data_fine") // nullable=true è il default
    private LocalDate dataFine;

    // Questa è la relazione Many-to-Many per i partecipanti alla sfida
    @ManyToMany
    @JoinTable(
        name = "partecipanti_sfida", // Nome della tabella di join nel DB
        joinColumns = @JoinColumn(name = "codice_sfida"), // Colonna che fa riferimento a Sfida
        inverseJoinColumns = @JoinColumn(name = "id_utente") // Colonna che fa riferimento a Utente
    )
    private Set<Utente> partecipanti = new HashSet<>(); // Usa un Set per evitare duplicati

    // Valuta se tenere o rimuovere questo campo.
    // Se la relazione Many-to-Many è la fonte della verità per i partecipanti,
    // questo campo potrebbe essere ridondante o creare incoerenze.
    // Potrebbe servire solo per una visualizzazione denormalizzata.
    @Column(name = "nome_partecipanti", columnDefinition = "json") // Rinominato per chiarezza, se lo tieni
    private String nomePartecipantiJson; // Sarà una stringa JSON, non una List<String> direttamente mappata

    // Se tieni nomePartecipantiJson, puoi avere dei metodi helper:
    public void setNomePartecipanti(List<String> nomi) {
        this.nomePartecipantiJson = convertToDatabaseColumn(nomi);
    }

    public List<String> getNomePartecipanti() {
        if (this.nomePartecipantiJson == null) {
            return List.of(); // Restituisci una lista vuota se non ci sono nomi
        }
        return convertToEntityAttribute(this.nomePartecipantiJson);
    }

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Errore nella conversione della lista in JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Errore nella conversione del JSON in lista", e);
        }
    }
}