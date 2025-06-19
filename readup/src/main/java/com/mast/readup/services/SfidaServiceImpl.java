package com.mast.readup.services;

import com.mast.readup.entities.Sfida;
import com.mast.readup.entities.Utente;
import com.mast.readup.repos.SfidaRepos;
import com.mast.readup.repos.UtenteRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SfidaServiceImpl implements SfidaService {

    @Autowired
    private final SfidaRepos sfidaRepos;

    @Autowired
    private final UtenteRepos utenteRepos;

    public SfidaServiceImpl(SfidaRepos sfidaRepos, UtenteRepos utenteRepos) {
        this.sfidaRepos = sfidaRepos;
        this.utenteRepos = utenteRepos;
    }

    @Override
    public List<Sfida> getAll() {
        return sfidaRepos.findAll();
    }

    @Override
    public Optional<Sfida> getById(Long codiceSfida) {
        return sfidaRepos.findById(codiceSfida);
    }

    @Override
    public Sfida aggiungiSfida(String nomeSfida, String descrizione, LocalDate dataInizio, LocalDate dataFine,
            Long idCreatore) {
        if (nomeSfida == null || nomeSfida.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della sfida non può essere vuoto.");
        }
        if (dataInizio == null || dataFine == null) {
            throw new IllegalArgumentException("Le date di inizio e fine non possono essere nulle.");
        }
        if (dataInizio.isAfter(dataFine)) {
            throw new IllegalArgumentException("La data di inizio non può essere successiva alla data di fine.");
        }
        if (idCreatore == null) {
            throw new IllegalArgumentException("L'ID del creatore non può essere null.");
        }

        // Trova l'utente creatore
        Utente creatore = utenteRepos.findById(idCreatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente creatore con ID " + idCreatore + " non trovato."));
        Sfida sfida = new Sfida();
        sfida.setNomeSfida(nomeSfida);
        sfida.setDescrizioneSfida(descrizione);
        sfida.setDataInizio(dataInizio);
        sfida.setDataFine(dataFine);
        sfida.setUtenteCreatore(creatore);
        return sfidaRepos.save(sfida);
    }

    @Override
    public void rimuoviSfida(Long codiceSfida) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rimuoviSfida'");
    }

    @Override
    public Sfida aggiornaSfida(Long codiceSfida, String nomeSfida, String descrizione, LocalDate dataInizio,
            LocalDate dataFine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'aggiornaSfida'");
    }

    @Override
    public Sfida partecipaSfida(Long codiceSfida, Long idUtente) {
        Sfida sfida = sfidaRepos.findById(codiceSfida)
                .orElseThrow(() -> new IllegalArgumentException("Sfida con ID " + codiceSfida + " non trovata."));

        Utente utente = utenteRepos.findById(idUtente)
                .orElseThrow(() -> new IllegalArgumentException("Utente con ID " + idUtente + " non trovato."));

        if (sfida.getNomePartecipanti() == null) {
            sfida.setNomePartecipanti(new ArrayList<>());
        }

        String nicknameUtente = utente.getNickname();
        if (nicknameUtente == null || nicknameUtente.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nickname dell'utente non può essere vuoto per la partecipazione alla sfida.");
        }

        if (LocalDate.now().isAfter(sfida.getDataFine())) {
            throw new IllegalStateException("Impossibile partecipare, la sfida è già terminata per data.");
        }

        // Controlla se l'utente è già partecipante usando il nickname
        if (sfida.getNomePartecipanti().contains(nicknameUtente)) {
            throw new IllegalArgumentException("L'utente con nickname '" + nicknameUtente + "' è già un partecipante di questa sfida.");
        }
        sfida.getNomePartecipanti().add(nicknameUtente);
        sfida.setNumPartecipanti(sfida.getNomePartecipanti().size());
        return sfidaRepos.save(sfida);
    }

    @Override
    public Sfida abbandonaSfida(Long codiceSfida, Long idUtente) {
        Sfida sfida = sfidaRepos.findById(codiceSfida)
                .orElseThrow(() -> new IllegalArgumentException("Sfida con ID " + codiceSfida + " non trovata."));

        Utente utente = utenteRepos.findById(idUtente)
                .orElseThrow(() -> new IllegalArgumentException("Utente con ID " + idUtente + " non trovato."));

        // Assicurati che la lista dei partecipanti sia inizializzata (dovrebbe esserlo)
        if (sfida.getNomePartecipanti() == null) {
            sfida.setNomePartecipanti(new ArrayList<>()); // Inizializza per sicurezza, anche se non dovrebbe essere null a questo punto
        }

        String nicknameUtente = utente.getNickname();
        if (nicknameUtente == null || nicknameUtente.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nickname dell'utente non può essere vuoto per l'abbandono della sfida.");
        }

        // Controlla se l'utente è effettivamente un partecipante usando il nickname
        if (!sfida.getNomePartecipanti().contains(nicknameUtente)) {
            throw new IllegalArgumentException("L'utente con nickname '" + nicknameUtente + "' non è un partecipante di questa sfida.");
        }

        sfida.getNomePartecipanti().remove(nicknameUtente);
        sfida.setNumPartecipanti(sfida.getNomePartecipanti().size());

        return sfidaRepos.save(sfida);
    }

    @Override
    public Sfida getDatiSfida(Long codiceSfida) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDatiSfida'");
    }
}
