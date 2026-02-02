package it.unicam.cs.mpgc.jtime123063.manager;

import it.unicam.cs.mpgc.jtime123063.model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReportManager {

    /**
     * Filtra le task in base a progetto e intervallo temporale.
     * LOGICA SPOSTATA QUI: Il Controller non deve sapere "come" si filtra.
     */
    public List<Task> getFilteredTasks(List<Task> allTasks, String projectName, LocalDate start, LocalDate end) {
        return allTasks.stream()
                .filter(t -> projectName.equals("Tutti i Progetti") ||
                        (t.getParentProject() != null && t.getParentProject().getName().equals(projectName)))
                .filter(t -> {
                    LocalDate d = t.getScheduledDate();
                    if (d == null) return (start == null && end == null);
                    return (start == null || !d.isBefore(start)) && (end == null || !d.isAfter(end));
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcola la varianza totale di una lista di task (Effettivo - Stimato).
     */
    public double getTotalVariance(List<Task> tasks) {
        return tasks.stream()
                .mapToDouble(t -> t.getEffectiveTime() - t.getEstimatedTime())
                .sum();
    }
}