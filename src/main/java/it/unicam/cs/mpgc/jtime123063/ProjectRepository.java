package it.unicam.cs.mpgc.jtime123063;

import java.io.IOException;
import java.util.List;

/**
 * Gestisce il salvataggio e il caricamento dei progetti su un supporto persistente.
 */
public interface ProjectRepository {

    /**
     * Salva la lista completa dei progetti.
     * * @param projects la lista di progetti da rendere persistente
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    void saveAll(List<Project> projects);

    /**
     * Carica tutti i progetti precedentemente salvati.
     * * @return la lista dei progetti caricati
     * @throws IOException se il file non esiste o è corrotto
     */
    List<Project> loadAll();
}