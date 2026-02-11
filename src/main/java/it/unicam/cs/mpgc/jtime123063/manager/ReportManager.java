package it.unicam.cs.mpgc.jtime123063.manager;

import it.unicam.cs.mpgc.jtime123063.model.Task;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    /**
     * Generates a formatted string representing the task timeline.
     * @param tasks The list of tasks to format.
     * @return A formatted string for display.
     */
    public String getFormattedTimeline(List<Task> tasks) {
        if (tasks.isEmpty()) return "\nNo activities found.";

        StringBuilder sb = new StringBuilder();
        LocalDate lastDate = null;

        // Sorting logic moved here
        List<Task> sorted = tasks.stream()
                .sorted(Comparator.comparing(Task::getScheduledDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        for (Task t : sorted) {
            LocalDate d = t.getScheduledDate();
            if (!Objects.equals(d, lastDate)) {
                sb.append(String.format("\n ---------- %s ---------- \n",
                        (d != null) ? d : "TO BE ASSIGNED"));
                lastDate = d;
            }

            String pInfo = (t.getParentProject() != null) ? "[" + t.getParentProject().getName() + "]" : "[Single]";
            sb.append(String.format(" %-22s %-28s | Est: %4.1fh | Eff: %4.1fh\n",
                    truncate(pInfo, 20), truncate(t.getTitle(), 25), t.getEstimatedTime(), t.getEffectiveTime()));
        }
        return sb.toString();
    }

    /**
     * Truncates text to a maximum length.
     * @param text The text to truncate.
     * @param max The maximum allowed length.
     * @return The truncated text.
     */
    private String truncate(String text, int max) {
        if (text == null) return "";
        return (text.length() <= max) ? text : text.substring(0, max - 3) + "...";
    }
}