package it.unicam.cs.mpgc.jtime123063;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SimpleProject implements Project {
    private final String id;
    private String name;
    private final List<Task> tasks;
    private boolean closed;

    public SimpleProject(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del progetto non può essere nullo o vuoto");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name.trim();
        this.tasks = new ArrayList<>();
        this.closed = false;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere nullo o vuoto");
        }
        this.name = name.trim();
    }

    @Override
    public List<Task> getTasks() {
        // Restituiamo una vista non modificabile per proteggere l'incapsulamento
        return Collections.unmodifiableList(tasks);
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Il task non può essere nullo");
        }
        this.tasks.add(task);
    }

    @Override
    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public boolean canBeClosed() {
        // Regola: vero solo se non ci sono task incompleti
        return tasks.stream().allMatch(Task::isCompleted);
    }

    @Override
    public void setClosed(boolean closed) {
        if (closed && !canBeClosed()) {
            throw new IllegalStateException("Impossibile chiudere il progetto: sono presenti attività non completate.");
        }
        this.closed = closed;
    }
}