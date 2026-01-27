package it.unicam.cs.mpgc.jtime123063;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class JTimeManager {
    private final ProjectManager projectManager;
    private final TaskManager taskManager;

    public JTimeManager(ProjectManager projectManager, TaskManager taskManager) {
        this.projectManager = projectManager;
        this.taskManager = taskManager;
    }

    /**
     * Sincronizza i task con i rispettivi progetti caricandoli dai repository.
     */
    public void syncData(List<Task> allTasks, boolean syncBidirectional) {
        List<Project> allProjects = projectManager.getAllProjects();
        XmlSimpleTaskRepository repo = (XmlSimpleTaskRepository) ((SimpleTaskManager) taskManager).getRepository();

        if (syncBidirectional) {
            for (Project p : allProjects) {
                if (p instanceof SimpleProject) { ((SimpleProject) p).clearTasks(); }
            }
        }

        for (Task task : allTasks) {
            String savedProjectId = repo.getProjectIdForTask(task.getId());
            if (savedProjectId != null && !savedProjectId.isEmpty()) {
                allProjects.stream()
                        .filter(p -> p.getId().equals(savedProjectId))
                        .findFirst()
                        .ifPresent(p -> {
                            task.setParentProject(p);
                            if (syncBidirectional && p instanceof SimpleProject) {
                                ((SimpleProject) p).loadTask(task);
                            }
                        });
            }
        }
    }







    public void syncData(List<Project> currentProjects) {
        List<Task> allTasks = taskManager.getAllTasks();
        XmlSimpleTaskRepository repo = (XmlSimpleTaskRepository) ((SimpleTaskManager) taskManager).getRepository();

        for (Project p : currentProjects) {
            if (p instanceof SimpleProject) { ((SimpleProject) p).clearTasks(); }
        }

        for (Task task : allTasks) {
            String savedProjectId = repo.getProjectIdForTask(task.getId());
            if (savedProjectId != null && !savedProjectId.isEmpty()) {
                currentProjects.stream()
                        .filter(p -> p.getId().equals(savedProjectId))
                        .findFirst()
                        .ifPresent(p -> {
                            task.setParentProject(p);
                            if (p instanceof SimpleProject) { ((SimpleProject) p).loadTask(task); }
                        });
            }
        }
    }

    /**
     * Gestisce il completamento di un task e resetta le ore se necessario.
     * Salva automaticamente sia il task che il progetto correlato.
     */
    public void updateTaskStatus(Task task, boolean completed) {
        task.setCompleted(completed);
        if (!completed) {
            task.setEffectiveTime(0.0);
        }
        taskManager.saveTask(task);

        // Se il task appartiene a un progetto, salviamo il progetto per aggiornare i totali
        if (task.getParentProject() != null) {
            projectManager.saveProject(task.getParentProject());
        }
    }

    /**
     * Aggiorna il tempo effettivo e persiste il cambiamento.
     */
    public void updateTaskEffectiveTime(Task task, Double time) {
        task.setEffectiveTime(time);
        taskManager.saveTask(task);
        if (task.getParentProject() != null) {
            projectManager.saveProject(task.getParentProject());
        }
    }

    public void addNewTaskToProject(Project project, Task task) {
        if (project != null) {
            task.setParentProject(project);
            project.addParentTask(task);
            taskManager.saveTask(task);
            projectManager.saveProject(project);
        } else {
            taskManager.saveTask(task);
        }
    }

    public void removeTaskPermanently(Task task) {
        if (task == null) return;
        if (task.getParentProject() != null) {
            task.getParentProject().removeParentTask(task);
            projectManager.saveProject(task.getParentProject());
        }
        taskManager.deleteTask(task);
    }

    // Metodi di utilità per filtri e statistiche
    public double getTotalEffortForDay(LocalDate date) {
        return taskManager.getAllTasks().stream()
                .filter(t -> date.equals(t.getScheduledDate()))
                .mapToDouble(Task::getEstimatedTime)
                .sum();
    }

    public List<Task> getAllSyncedTasks() {
        List<Task> allTasks = taskManager.getAllTasks();
        // Chiamiamo la versione di syncData che accetta i Task
        // Passiamo 'true' così riempie anche le liste dei progetti per sicurezza
        syncData(allTasks, true);
        return allTasks;
    }

    public List<Project> getAllProjects() {
        return projectManager.getAllProjects();
    }

    public void saveProject(Project newProject) {
        projectManager.saveProject(newProject);
    }
}