package it.unicam.cs.mpgc.jtime123063;

import java.time.LocalDate;
import java.util.UUID;

public class SimpleTask implements Task {
    private final String id;
    private String title;
    private double estimatedTime;
    private double effectiveTime;
    private boolean completed;
    private LocalDate scheduledDate;

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

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    //         !!VEDI!!
    @Override
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere nullo o vuoto");
        }
        this.title= title;

    }

    @Override
    public double getEstimatedTime() {
        return this.estimatedTime;
    }

    //          !!VEDI!!
    @Override
    public void setEstimatedTime(double hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("La stima del tempo non può essere negativa");
        }
        this.estimatedTime= hours;
    }

    @Override
    public double getEffectiveTime() {
        return effectiveTime;
    }

    //       !!VEDI!!
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

    // SET COMPLETED = COMPLETED O = TRUE ????   !!VEDI!!
    @Override
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    //              !!VEDI!!
    @Override
    public void setScheduledDate(LocalDate date) {
        this.scheduledDate = date;
    }

    @Override
    public double getTimeVariance() {
        return this.effectiveTime - this.estimatedTime;
    }
}