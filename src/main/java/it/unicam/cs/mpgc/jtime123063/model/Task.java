package it.unicam.cs.mpgc.jtime123063.model;

import java.time.LocalDate;

/**
 * Represents an atomic task.
 */
public interface Task {

    /**
     * Gets the unique identifier of the task.
     * @return The task ID as a String.
     */
    String getId();

    /**
     * Gets the task title.
     * @return The title of the task.
     */
    String getTitle();

    /**
     * Sets the task title.
     * @param title The new title; cannot be null or empty.
     * @throws IllegalArgumentException if title is null or blank.
     */
    void setTitle(String title);

    /**
     * Gets the estimated time for completion.
     * @return The estimated time in hours.
     */
    double getEstimatedTime();

    /**
     * Sets the estimated time for the task.
     * @param hours The estimated time in hours; must be non-negative.
     * @throws IllegalArgumentException if hours is less than 0.
     */
    void setEstimatedTime(double hours);

    /**
     * Gets the effective time spent on the task.
     * @return The effective time in hours.
     */
    double getEffectiveTime();

    /**
     * Sets the effective time spent on the task.
     * @param hours The actual time spent in hours; must be non-negative.
     * @throws IllegalArgumentException if hours is less than 0.
     */
    void setEffectiveTime(double hours);

    /**
     * Checks if the task is completed.
     * @return true if completed, false otherwise.
     */
    boolean isCompleted();

    /**
     * Sets the completion status of the task.
     * @param completed true to mark as finished, false otherwise.
     */
    void setCompleted(boolean completed);

    /**
     * Gets the scheduled execution date.
     * @return The scheduled date, or null if not programmed.
     */
    LocalDate getScheduledDate();

    /**
     * Sets the scheduled execution date.
     * @param date The date to perform the task.
     */
    void setScheduledDate(LocalDate date);

    /**
     * Calculates the difference between effective and estimated time.
     * @return The time variance (effective - estimated).
     */
    double getTimeVariance();

    /**
     * Gets the project this task belongs to.
     * @return The parent project, or null if independent.
     */
    Project getParentProject();

    /**
     * Sets the project this task belongs to.
     * @param project The parent project.
     */
    void setParentProject(Project project);
}