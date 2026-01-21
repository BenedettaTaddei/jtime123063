package it.unicam.cs.mpgc.jtime123063;

import java.util.List;

/**
 * Rappresenta un progetto composto da un insieme di attività (task).
 * Un progetto agisce come contenitore logico e definisce le regole di business
 * per la gestione del ciclo di vita delle attività, inclusa la validazione
 * dello stato di chiusura.
 */
public interface Project {

    /**
     * Restituisce l'identificativo univoco del progetto.
     *
     * @return l'ID del progetto come String
     */
    String getId();

    /**
     * Restituisce il nome del progetto.
     *
     * @return il nome del progetto
     */
    String getName();

    /**
     * Imposta il nome del progetto.
     *
     * @param name il nuovo nome del progetto, non può essere nullo o vuoto
     * @throws IllegalArgumentException se il nome è nullo o composto solo da spazi
     */
    void setName(String name);

    /**
     * Restituisce la lista dei task associati al progetto.
     * La lista restituita dovrebbe essere una vista non modificabile per garantire l'incapsulamento.
     *
     * @return lista dei task associati
     */
    List<Task> getTasks();

    /**
     * Aggiunge un'attività al progetto.
     *
     * @param task il task da aggiungere, non può essere nullo
     * @throws IllegalArgumentException se il task fornito è nullo
     */
    void addTask(Task task);

    /**
     * Rimuove un'attività specifica dal progetto.
     *
     * @param task il task da rimuovere
     */
    void removeTask(Task task);

    /**
     * Verifica se il progetto è attualmente nello stato chiuso.
     *
     * @return true se il progetto è chiuso, false se è attivo
     */
    boolean isClosed();

    /**
     * Tenta di cambiare lo stato di chiusura del progetto.
     * La chiusura è consentita solo se non vi sono attività pendenti.
     *
     * @param closed true per tentare di chiudere il progetto, false per riaprirlo
     * @throws IllegalStateException se si tenta di chiudere il progetto (closed=true)
     * mentre canBeClosed() restituisce false
     */
    void setClosed(boolean closed);

    /**
     * Verifica se il progetto soddisfa i requisiti necessari per essere chiuso.
     * Secondo la specifica, un progetto è chiudibile solo se tutte le attività
     * associate risultano completate.
     *
     * @return true se tutti i task sono completati, false se esiste almeno un task pendente
     */
    boolean canBeClosed();

    double getTotalEstimatedTime();

    double getTotalEffectiveTime();
}