package it.unicam.cs.mpgc.jtime123063;

import java.util.List;

public interface ProjectManager {
    void addProject(Project project);
    void removeProject(Project project);
    List<Project> getAllProjects();
}