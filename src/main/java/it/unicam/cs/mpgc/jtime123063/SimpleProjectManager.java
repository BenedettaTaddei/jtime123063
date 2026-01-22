package it.unicam.cs.mpgc.jtime123063;

import java.util.List;

public class SimpleProjectManager implements ProjectManager {

    private final ProjectRepository projectRepository;

    public SimpleProjectManager(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void addProject(Project project) {
        this.projectRepository.save(project);
    }

    @Override
    public void removeProject(Project project) {
        this.projectRepository.remove(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return this.projectRepository.findAll();
    }
}