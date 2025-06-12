package com.mast.readup.services;

import com.mast.readup.entities.Sfida;
import com.mast.readup.repos.SfidaRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante per le transazioni

import java.util.List;
import java.util.Optional;

@Service // Indica a Spring che questa è una classe di servizio
public class SfidaServiceImpl implements SfidaService {

    @Autowired // Inietta il repository tramite il costruttore
    private final SfidaRepos sfidaRepos;

    public SfidaServiceImpl(SfidaRepos sfidaRepos) {
        this.sfidaRepos = sfidaRepos;
    }

    @Override
    @Transactional(readOnly = true) // Ottimizza per le operazioni di sola lettura
    public List<Sfida> getAll() {
        return sfidaRepos.findAll();
    }

    @Override
    public Optional<Sfida> getById(Long codiceSfida) {
        return sfidaRepos.findById(codiceSfida);
    }

    // Qui verranno aggiunti i metodi aggiungiSfida, rimuoviSfida, aggiornaSfida,
    // partecipaSfida, annullaPartecipaSfida in futuro,
    // e dovranno essere annotati con @Transactional
}
