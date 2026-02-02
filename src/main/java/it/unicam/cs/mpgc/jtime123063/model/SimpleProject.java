package it.unicam.cs.mpgc.jtime123063.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

public class SimpleProject implements Project {
    private final String id;
    private String name;
    private final List<Task> tasks;
    private boolean closed;

    /**
     * Costruttore per nuovi progetti (genera UUID).
     */
    public SimpleProject(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del progetto non può essere nullo o vuoto");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name.trim();
        this.tasks = new ArrayList<>();
        this.closed = false;
    }

    /**
     * Costruttore per la persistenza (mantiene l'ID originale).
     */
    public SimpleProject(String id, String name) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID non può essere nullo");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere nullo");
        }
        this.id = id;
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
        return Collections.unmodifiableList(tasks);
    }
    @Override
    public int getTaskCount() {
        return tasks.size();
    }

    @Override
    public void addParentTask(Task task) {
        if (this.isClosed())
            throw new IllegalStateException("Non puoi aggiungere task a un progetto chiuso");
        if (task == null) {
            throw new IllegalArgumentException("Il task non può essere nullo");
        }
        this.tasks.add(task);
        task.setParentProject(this);
    }

    @Override
    public void removeParentTask(Task task) {
        if (this.isClosed()) {
            throw new IllegalStateException("Non è possibile rimuovere task da un progetto chiuso.");
        }
        if (task != null && this.tasks.remove(task)) {
            task.setParentProject(null);
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public boolean canBeClosed() {
        if (tasks.isEmpty()) return true;
        return tasks.stream().allMatch(Task::isCompleted);
    }

    @Override
    public void setClosed(boolean closed) {
        if (closed && !canBeClosed()) {
            throw new IllegalStateException("Impossibile chiudere il progetto: sono presenti attività non completate.");
        }
        this.closed = closed;
    }

    @Override
    public double getTotalEstimatedTime() {
//        if (tasks == null || tasks.isEmpty()) {
//            return 0.0;
//        }
        return tasks.stream().mapToDouble(Task::getEstimatedTime).sum();
    }

    @Override
    public double getTotalEffectiveTime() {
        return tasks.stream().mapToDouble(Task::getEffectiveTime).sum();
    }

    /**
     * Calcola la percentuale di avanzamento in base ai task completati.
     */
    public double getCompletionPercentage() {
        if (tasks.isEmpty()) return 0.0;
        long completedCount = tasks.stream().filter(Task::isCompleted).count();
        return (double) completedCount / tasks.size() * 100.0;
    }

    /**
     * Un progetto è considerato "Completato" logicamente se tutti i task lo sono,
     * indipendentemente dal flag 'closed' amministrativo.
     */
    public boolean isCompleted() {
        return !tasks.isEmpty() && tasks.stream().allMatch(Task::isCompleted);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleProject)) return false;
        SimpleProject that = (SimpleProject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    /**
     * Metodo tecnico usato dal Manager durante il caricamento XML.
     * Permette di ricostruire i legami anche se il progetto è già chiuso.
     */
    public void loadTask(Task task) {
        if (task == null) return;
        if (!this.tasks.contains(task)) {
            this.tasks.add(task);
        }
        task.setParentProject(this);
    }
    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public void clearTasks() {
        this.tasks.clear();
    }

}