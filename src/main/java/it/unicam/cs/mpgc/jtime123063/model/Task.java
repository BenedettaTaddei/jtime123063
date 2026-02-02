package it.unicam.cs.mpgc.jtime123063.model;

import java.time.LocalDate;

/**
 * Rappresenta un'attività atomica (task) all'interno di un progetto.
 * Un task permette di tracciare il tempo stimato rispetto a quello effettivo,
 * gestire lo stato di completamento e pianificare l'esecuzione in una data specifica.
 */
public interface Task {

    /**
     * Restituisce l'identificativo univoco del task.
     *
     * @return l'id del task come String
     */
    String getId();

    /**
     * Restituisce il titolo del task.
     *
     * @return il titolo del task
     */
    String getTitle();

    /**
     * Imposta il titolo del task.
     *
     * @param title il nuovo titolo, non può essere nullo o vuoto
     * @throws IllegalArgumentException se il titolo è nullo o composto solo da spazi
     */
    void setTitle(String title);

    /**
     * Restituisce il tempo stimato per lo svolgimento dell'attività.
     *
     * @return il tempo stimato in ore
     */
    double getEstimatedTime();

    /**
     * Imposta la stima del tempo necessario.
     *
     * @param hours il tempo stimato in ore, deve essere non negativo
     * @throws IllegalArgumentException se hours è minore di 0
     */
    void setEstimatedTime(double hours);

    /**
     * Restituisce il tempo effettivamente impiegato per completare l'attività.
     *
     * @return il tempo effettivo in ore
     */
    double getEffectiveTime();

    /**
     * Imposta il tempo effettivamente speso.
     *
     * @param hours il tempo speso in ore, deve essere non negativo
     * @throws IllegalArgumentException se hours è minore di 0
     */
    void setEffectiveTime(double hours);

    /**
     * Verifica se l'attività è stata completata.
     *
     * @return true se il task è completato, false altrimenti
     */
    boolean isCompleted();

    /**
     * Imposta lo stato di completamento del task.
     *
     * @param completed true per segnare come completato, false altrimenti
     */
    void setCompleted(boolean completed);

    /**
     * Restituisce la data in cui l'attività è stata pianificata.
     *
     * @return la data pianificata, o null se non ancora programmata
     */
    LocalDate getScheduledDate();

    /**
     * Imposta la data di pianificazione per lo svolgimento del task.
     *
     * @param date la data pianificata
     */
    void setScheduledDate(LocalDate date);

    /**
     * Calcola la differenza tra il tempo speso e il tempo stimato.
     * Un valore positivo indica che si è impiegato più tempo del previsto.
     * Un valore negativo se l'attività è stata più rapida del previsto
     *
     * @return lo scostamento temporale
     */
    double getTimeVariance();
    /**
     * Restituisce il progetto a cui appartiene il task.
     * * @return il progetto genitore, o null se il task è indipendente.
     */
    Project getParentProject();

    /**
     * Imposta il progetto a cui appartiene questo task.
     * * @param project il progetto genitore
     */
    void setParentProject(Project project);
}