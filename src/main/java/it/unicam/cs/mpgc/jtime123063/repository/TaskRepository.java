package it.unicam.cs.mpgc.jtime123063.repository;

import it.unicam.cs.mpgc.jtime123063.model.Task;

import java.util.List;

/**
 * Gestisce la persistenza di tutti i Task (sia indipendenti che di progetto)
 * utilizzando un approccio centralizzato.
 */
public interface TaskRepository {

    /**
     * Salva o aggiorna un task nel supporto persistente.
     */
    void save(Task task);

    /**
     * Rimuove un task dal supporto persistente.
     * @return true se rimosso, false altrimenti
     */
    boolean remove(Task task);

    /**
     * Carica tutti i task salvati nel sistema.
     */
    List<Task> findAll();

    /**
     * Elimina definitivamente tutti i task salvati.
     */
    void clearAll();
}