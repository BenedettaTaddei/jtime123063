package it.unicam.cs.mpgc.jtime123063.model;

import java.util.List;

/**
 * Represents a project composed of a set of tasks.
 */
public interface Project {

    /**
     * Gets the unique identifier of the project.
     * @return The project ID as a String.
     */
    String getId();

    /**
     * Gets the project name.
     * @return The name of the project.
     */
    String getName();

    /**
     * Sets the project name.
     * @param name The new project name; cannot be null or empty.
     * @throws IllegalArgumentException if the name is null or blank.
     */
    void setName(String name);

    /**
     * Returns an unmodifiable view of the tasks associated with the project.
     * @return A list of associated tasks.
     */
    List<Task> getTasks();

    /**
     * Gets the total number of tasks in the project.
     * @return The task count.
     */
    int getTaskCount();

    /**
     * Adds an existing task to this project and establishes a bidirectional link.
     * @param task The task to associate with this project.
     * @throws IllegalArgumentException if the task is null.
     * @throws IllegalStateException if the project is closed and cannot accept new tasks.
     */
    void addParentTask(Task task);

    /**
     * Removes the link between this project and the specified task.
     * The task is not deleted but becomes "unassigned".
     * @param task The task to disconnect.
     * @throws IllegalStateException if the project is closed.
     */
    void removeParentTask(Task task);

    /**
     * Checks if the project is currently in a closed state.
     * @return true if closed, false if active.
     */
    boolean isClosed();

    /**
     * Attempts to change the project's closure state.
     * @param closed true to close the project, false to reopen it.
     * @throws IllegalStateException if attempting to close while canBeClosed() is false.
     */
    void setClosed(boolean closed);

    /**
     * Verifies if the project meets the requirements for closure.
     * @return true if all tasks are completed or list is empty, false otherwise.
     */
    boolean canBeClosed();

    /**
     * Calculates the sum of estimated hours for all tasks in the project.
     * @return Total estimated time.
     */
    double getTotalEstimatedTime();

    /**
     * Calculates the sum of effective hours spent on all tasks in the project.
     * @return Total effective time.
     */
    double getTotalEffectiveTime();

    /**
     * Calculates the completion percentage based on the number of completed tasks.
     * @return A value between 0.0 and 100.0.
     */
    double getCompletionPercentage();

    /**
     * Logical check to see if all tasks are completed.
     * @return true if there is at least one task and all are completed.
     */
    boolean isCompleted();

    /**
     * Removes all tasks from the project.
     */
    void clearTasks();
}