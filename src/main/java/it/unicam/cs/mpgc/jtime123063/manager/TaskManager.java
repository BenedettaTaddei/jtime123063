package it.unicam.cs.mpgc.jtime123063.manager;

import it.unicam.cs.mpgc.jtime123063.model.Task;

import java.util.List;

public interface TaskManager {
    void saveTask(Task task);
    void deleteTask(Task task);
    List<Task> getAllTasks();
}