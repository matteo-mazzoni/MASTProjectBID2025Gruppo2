package com.mast.readup.services;

import com.mast.readup.entities.Sfida;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;


public interface SfidaService {
    List<Sfida> getAll();
    Optional<Sfida> getById(Long codiceSfida);
    Sfida aggiungiSfida(String nomeSfida, String descrizione, LocalDate dataInizio, LocalDate dataFine, Long idCreatore);
    void rimuoviSfida(Long codiceSfida);
    Sfida aggiornaSfida(Long codiceSfida, String nomeSfida, String descrizione, LocalDate dataInizio, LocalDate dataFine);
    Sfida partecipaSfida(Long codiceSfida, Long idUtente);
    Sfida abbandonaSfida(Long codiceSfida, Long idUtente);
}