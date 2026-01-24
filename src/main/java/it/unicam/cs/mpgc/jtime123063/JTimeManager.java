package it.unicam.cs.mpgc.jtime123063;

public class JTimeManager {
    private final ProjectManager projectManager;
    private final TaskManager taskManager;

    public JTimeManager(ProjectManager projectManager, TaskManager taskManager) {
        this.projectManager = projectManager;
        this.taskManager = taskManager;
    }

    public void deleteProjectWithAllTasks(Project project) {
        //  pulizia delle task
        project.getTasks().forEach(taskManager::deleteTask);

        // Delega al ProjectManager la rimozione del progetto
        projectManager.deleteProjectRecord(project);
    }

    public void addNewTaskToProject(Project project, Task task) {
        project.addParentTask(task);
        taskManager.saveTask(task);
        projectManager.saveProject(project);
    }
    public void removeTaskToProject(Project project, Task task) {
        project.removeParentTask(task);
        taskManager.deleteTask(task);
        projectManager.saveProject(project);
    }
}