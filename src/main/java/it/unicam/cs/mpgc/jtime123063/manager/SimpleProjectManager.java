package it.unicam.cs.mpgc.jtime123063.manager;

import it.unicam.cs.mpgc.jtime123063.model.Project;
import it.unicam.cs.mpgc.jtime123063.repository.ProjectRepository;

import java.util.List;

public class SimpleProjectManager implements ProjectManager {

    private final ProjectRepository projectRepository;

    public SimpleProjectManager(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void saveProject(Project project) {
        this.projectRepository.save(project);
    }

    @Override
    public void deleteProject(Project project) {
        if (project == null){
            throw  new IllegalArgumentException("impossibile eliminare un progetto nullo ");
        }
        this.projectRepository.remove(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return this.projectRepository.findAll();
    }
}