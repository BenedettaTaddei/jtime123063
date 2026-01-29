package it.unicam.cs.mpgc.jtime123063;

import java.util.List;

public interface ProjectManager {
    void saveProject(Project project);
    void deleteProject(Project project);
    List<Project> getAllProjects();
}