package it.unicam.cs.mpgc.jtime123063;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manager coordinatore per la logica di business che coinvolge sia Task che Progetti.
 */
public class JTimeManager {
    private final ProjectManager projectManager;
    private final TaskManager taskManager;

    public JTimeManager(ProjectManager projectManager, TaskManager taskManager) {
        this.projectManager = projectManager;
        this.taskManager = taskManager;
    }

    /**
     * Aggiunge un task a un progetto e sincronizza il salvataggio su entrambi i repository.
     */
    public void addNewTaskToProject(Project project, Task task) {
        if (project == null || task == null) return;

        task.setParentProject(project);
        project.addParentTask(task);

        // Persistenza atomica (su entrambi i file XML)
        taskManager.saveTask(task);
        projectManager.saveProject(project);
    }

    /**
     * Rimuove un task da un progetto e lo elimina permanentemente.
     */
    public void removeTaskToProject(Project project, Task task) {
        if (project == null || task == null) return;

        project.removeParentTask(task);
        taskManager.deleteTask(task);
        projectManager.saveProject(project);
    }

    /**
     * Elimina un progetto e a cascata tutte le sue attività.
     */
    public void deleteProjectWithAllTasks(Project project) {
        if (project == null) return;

        // Pulizia ricorsiva dei task
        project.getTasks().forEach(t -> taskManager.deleteTask(t));
        projectManager.deleteProjectRecord(project);
    }

    /**
     * REQUISITO PIANIFICAZIONE: Calcola il carico di lavoro totale per un giorno.
     * Utile per verificare se l'utente è sovraccarico in una data specifica.
     */
    public double getTotalEffortForDay(LocalDate date) {
        return taskManager.getAllTasks().stream()
                .filter(t -> t.getScheduledDate() != null && t.getScheduledDate().equals(date))
                .mapToDouble(Task::getEstimatedTime)
                .sum();
    }

    /**
     * REQUISITO REPORTISTICA: Restituisce i task pianificati in un intervallo temporale.
     */
    public List<Task> getTasksInPeriod(LocalDate start, LocalDate end) {
        return taskManager.getAllTasks().stream()
                .filter(t -> t.getScheduledDate() != null &&
                        (t.getScheduledDate().isEqual(start) || t.getScheduledDate().isAfter(start)) &&
                        (t.getScheduledDate().isEqual(end) || t.getScheduledDate().isBefore(end)))
                .collect(Collectors.toList());
    }
    //    public void syncData() {
//        List<Task> allTasks = taskManager.getAllTasks();
//        List<Project> allProjects = projectManager.getAllProjects();
//
//        XmlSimpleTaskRepository repo = (XmlSimpleTaskRepository) ((SimpleTaskManager) taskManager).getRepository();
//
//        for (Task task : allTasks) {
//            String savedProjectId = repo.getProjectIdForTask(task.getId());
//
//            //savedProjectId = null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
//            if (savedProjectId != null && !savedProjectId.isEmpty()) {
//                allProjects.stream().filter(p -> p.getId().equals(savedProjectId))
//                        .findFirst().ifPresent(p -> {((SimpleProject) p).loadTask(task);});
//            }
//        }
//    }
//    public void syncData() {
//        List<Task> allTasks = taskManager.getAllTasks();
//        List<Project> allProjects = projectManager.getAllProjects();
//
//        for (Project p : allProjects) {
//            // Cicliamo sulle task che il progetto "sa" di avere (lette dal suo XML)
//            for (Task taskDelProgetto : p.getTasks()) {
//                // Cerchiamo la task corrispondente nella lista globale usando l'ID
//                allTasks.stream()
//                        .filter(gt -> gt.getId().equals(taskDelProgetto.getId()))
//                        .findFirst()
//                        .ifPresent(gt -> {
//                            // COLLEGIAMO IL PADRE: questo trasformerà [Libero] nel nome del progetto
//                            gt.setParentProject(p);
//                        });
//        }
//    }
// Aggiungi (List<Task> allTasks) qui dentro
    public void syncData(List<Task> allTasks) {
        List<Project> allProjects = projectManager.getAllProjects();
        XmlSimpleTaskRepository repo = (XmlSimpleTaskRepository) ((SimpleTaskManager) taskManager).getRepository();

        for (Task task : allTasks) {
            String savedProjectId = repo.getProjectIdForTask(task.getId());

            if (savedProjectId != null && !savedProjectId.isEmpty()) {
                allProjects.stream()
                        .filter(p -> p.getId().equals(savedProjectId))
                        .findFirst()
                        .ifPresent(p -> {
                            // Colleghiamo il progetto all'oggetto task che è nella lista
                            task.setParentProject(p);
                        });
            }
        }
    }
}