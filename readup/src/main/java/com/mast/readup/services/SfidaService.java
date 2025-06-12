package com.mast.readup.services;

import com.mast.readup.entities.Sfida;

import java.util.List;
import java.util.Optional;

public interface SfidaService {
    Optional<Sfida> getById(Long codiceSfida);
    List<Sfida> getAll();
}