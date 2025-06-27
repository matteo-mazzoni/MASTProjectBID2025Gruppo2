package com.mast.readup.services;

import com.mast.readup.entities.Libro;
import java.util.List;

public interface LibreriaService {
    List<Libro> getLibriByUtenteId(Long idUtente);
}