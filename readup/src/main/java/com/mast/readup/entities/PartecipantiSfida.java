package com.mast.readup.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the "partecipanti_sfida" table.
 * Stores information about users participating in challenges.
 */
@Entity
@Table(name = "partecipanti_sfida")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartecipantiSfida {

    // Auto-generated primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lista_partecipanti_id")
    private Long listaPartecipantiId;

    // Reference to the user participating in the challenge
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente")
    private Utente utente;

    // Challenge code the user is participating in
    @Column(name = "codice_sfida")
    private String codiceSfida;
}