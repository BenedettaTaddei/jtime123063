package it.unicam.cs.mpgc.jtime123063;

import java.util.List;

public class SimpleTaskManager implements TaskManager {

    private final TaskRepository taskRepository;

    public SimpleTaskManager(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void saveTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Impossibile salvare una task null");
        }
        this.taskRepository.save(task);
    }

    @Override
    public void deleteTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Impossibile eliminare una task null");
        }
        if (task.getParentProject() != null) {
            task.getParentProject().removeParentTask(task);
        }
        this.taskRepository.remove(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return this.taskRepository.findAll();
    }
    public TaskRepository getRepository() {
        return this.taskRepository; // o il nome della variabile che usi per il repository
    }
}