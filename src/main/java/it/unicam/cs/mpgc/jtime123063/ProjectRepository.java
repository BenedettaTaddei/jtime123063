package it.unicam.cs.mpgc.jtime123063;

import java.util.List;

/**
 * Gestisce la persistenza dei progetti.
 * Segue lo stesso standard della TaskRepository per coerenza di sistema.
 */
public interface ProjectRepository {

    /**
     * Salva o aggiorna un progetto.
     * @param project il progetto da rendere persistente
     */
    void save(Project project);

    /**
     * Rimuove un progetto dal supporto persistente.
     * @param project il progetto da rimuovere
     * @return true se rimosso con successo
     */
    boolean remove(Project project);

    /**
     * Carica tutti i progetti salvati (senza i task, che verranno collegati dal Manager).
     * @return la lista dei progetti caricati
     */
    List<Project> findAll();

    /**
     * Rimuove tutti i progetti salvati.
     */
    void clearAll();
}