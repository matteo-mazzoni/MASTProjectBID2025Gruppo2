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

        // Find the creator user by ID
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
        Sfida sfida = sfidaRepos.findById(codiceSfida)
                .orElseThrow(() -> new IllegalArgumentException("Sfida con ID " + codiceSfida + " non trovata."));
        sfidaRepos.delete(sfida);
    }

    @Override
    public Sfida aggiornaSfida(Long codiceSfida, String nomeSfida, String descrizione, LocalDate dataInizio,
            LocalDate dataFine) {
        Sfida sfida = sfidaRepos.findById(codiceSfida)
                .orElseThrow(() -> new IllegalArgumentException("Sfida con ID " + codiceSfida + " non trovata."));

        if (nomeSfida != null && !nomeSfida.trim().isEmpty()) {
            sfida.setNomeSfida(nomeSfida);
        } else if (nomeSfida != null) {
            throw new IllegalArgumentException("Il nome della sfida non può essere vuoto.");
        }

        if (descrizione != null) {
            sfida.setDescrizioneSfida(descrizione);
        }

        LocalDate currentDataInizio = (dataInizio != null) ? dataInizio : sfida.getDataInizio();
        LocalDate currentDataFine = (dataFine != null) ? dataFine : sfida.getDataFine();

        if (currentDataInizio.isAfter(currentDataFine)) {
            throw new IllegalArgumentException("La data di inizio non può essere successiva alla data di fine.");
        }

        if (dataInizio != null) {
            sfida.setDataInizio(dataInizio);
        }
        if (dataFine != null) {
            sfida.setDataFine(dataFine);
        }

        return sfidaRepos.save(sfida);
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
        // verifies if the nickname of the user is not null or empty
        String nicknameUtente = utente.getNickname();
        if (nicknameUtente == null || nicknameUtente.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nickname dell'utente non può essere vuoto per la partecipazione alla sfida.");
        }

        if (LocalDate.now().isAfter(sfida.getDataFine())) {
            throw new IllegalStateException("Impossibile partecipare, la sfida è già terminata per data.");
        }
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

        if (sfida.getNomePartecipanti() == null) {
            sfida.setNomePartecipanti(new ArrayList<>());
        }

        String nicknameUtente = utente.getNickname();
        if (nicknameUtente == null || nicknameUtente.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nickname dell'utente non può essere vuoto per l'abbandono della sfida.");
        }

        if (!sfida.getNomePartecipanti().contains(nicknameUtente)) {
            throw new IllegalArgumentException("L'utente con nickname '" + nicknameUtente + "' non è un partecipante di questa sfida.");
        }

        sfida.getNomePartecipanti().remove(nicknameUtente);
        sfida.setNumPartecipanti(sfida.getNomePartecipanti().size());

        return sfidaRepos.save(sfida);
    }
}
