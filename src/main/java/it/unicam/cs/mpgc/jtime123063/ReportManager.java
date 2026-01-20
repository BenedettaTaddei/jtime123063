package it.unicam.cs.mpgc.jtime123063;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsabile dell'elaborazione dei dati per la reportistica e pianificazione.
 * Analizza i progetti e le attività per fornire statistiche e calcoli di carico di lavoro.
 */
public class ReportManager {

    /**
     * Calcola l'impegno totale previsto per un giorno specifico.
     * Gestione della pianificazione: "tenendo conto dell’impegno totale richiesto".
     *
     * @param date la data da analizzare
     * @param allProjects la lista di tutti i progetti nel sistema
     * @return la somma delle ore stimate (estimatedTime) dei task pianificati per quel giorno
     */
    public double calculateDailyWorkload(LocalDate date, List<Project> allProjects) {
        if (date == null || allProjects == null) return 0.0;

        return allProjects.stream()
                .flatMap(p -> p.getTasks().stream())
                .filter(t -> date.equals(t.getScheduledDate()))
                .mapToDouble(Task::getEstimatedTime)
                .sum();
    }

    /**
     * Filtra tutte le attività completate in un determinato intervallo temporale.
     *
     * @param start data inizio
     * @param end data fine
     * @param allProjects lista progetti
     * @return lista dei task completati nell'intervallo
     */
    public List<Task> getTasksByInterval(LocalDate start, LocalDate end, List<Project> allProjects) {
        return allProjects.stream()
                .flatMap(p -> p.getTasks().stream())
                .filter(Task::isCompleted)
                .filter(t -> t.getScheduledDate() != null &&
                        !t.getScheduledDate().isBefore(start) &&
                        !t.getScheduledDate().isAfter(end))
                .collect(Collectors.toList());
    }

    /**
     * Calcola la varianza temporale totale per un intero progetto.
     * Permette di valutare a posteriori la differenza rispetto a quanto stimato.
     *
     * @param project il progetto da analizzare
     * @return la differenza totale tra tempo effettivo e stimato
     */
    public double getProjectTotalVariance(Project project) {
        return project.getTasks().stream()
                .mapToDouble(Task::getTimeVariance)
                .sum();
    }
}