package it.unicam.cs.mpgc.jtime123063.model;

import java.time.LocalDate;
import java.util.UUID;
import java.util.Objects;

/**
 * Standard implementation of the Task interface.
 * Provides basic functionality for tracking task progress, timing, and project association.
 */
public class SimpleTask implements Task {

    private final String id;
    private String title;
    private Project parentProject;
    private double estimatedTime;
    private double effectiveTime;
    private boolean completed;
    private LocalDate scheduledDate;

    /**
     * Constructor for creating new tasks with a generated unique ID.
     * * @param title         The title of the task.
     * @param estimatedTime The estimated duration in hours.
     * @throws IllegalArgumentException if title is null/empty or estimatedTime is negative.
     */
    public SimpleTask(String title, double estimatedTime) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere nullo o vuoto");
        }
        if (estimatedTime < 0) {
            throw new IllegalArgumentException("La stima del tempo non può essere negativa");
        }

        this.id = UUID.randomUUID().toString();
        this.title = title.trim();
        this.estimatedTime = estimatedTime;
        this.completed = false;
        this.effectiveTime = 0;
    }

    /**
     * Constructor used by the XML Parser to reconstruct existing tasks.
     * * @param id            The unique identifier of the task.
     * @param title         The title of the task.
     * @param estimatedTime The estimated duration in hours.
     * @param effectiveTime The actual time spent in hours.
     * @param completed     The completion status of the task.
     * @param date          The scheduled execution date.
     * @throws IllegalArgumentException if parameters are invalid (null IDs, negative times, etc.).
     */
    public SimpleTask(String id, String title, double estimatedTime,
                      double effectiveTime, boolean completed, LocalDate date) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID nullo");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere nullo o vuoto");
        }
        if (estimatedTime < 0) {
            throw new IllegalArgumentException("La stima del tempo non può essere negativa");
        }
        if (effectiveTime < 0) {
            throw new IllegalArgumentException("Il tempo effettivo non può essere negativo");
        }
        this.id = id;
        this.title = title.trim();
        this.estimatedTime = estimatedTime;
        this.effectiveTime = effectiveTime;
        this.completed = completed;
        this.scheduledDate = date;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere nullo o vuoto");
        }
        this.title = title.trim();
    }

    @Override
    public double getEstimatedTime() {
        return this.estimatedTime;
    }

    @Override
    public void setEstimatedTime(double hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("La stima del tempo non può essere negativa");
        }
        this.estimatedTime = hours;
    }

    @Override
    public double getEffectiveTime() {
        return effectiveTime;
    }

    @Override
    public void setEffectiveTime(double hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("La durata effettiva di una Task non può essere negativa");
        }
        this.effectiveTime = hours;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    @Override
    public void setScheduledDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La data programmata non può essere nulla");
        }
        this.scheduledDate = date;
    }

    @Override
    public Project getParentProject() {
        return this.parentProject;
    }

    @Override
    public void setParentProject(Project project) {
        this.parentProject = project;
    }

    @Override
    public double getTimeVariance() {
        return this.effectiveTime - this.estimatedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleTask)) return false;
        SimpleTask that = (SimpleTask) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String projectInfo = (parentProject != null) ? " [" + parentProject.getName() + "]" : " [Libero]";
        return title + projectInfo + (completed ? " (Completato)" : "");
    }
}