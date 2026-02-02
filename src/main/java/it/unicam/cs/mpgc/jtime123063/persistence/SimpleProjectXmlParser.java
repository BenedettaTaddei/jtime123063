package it.unicam.cs.mpgc.jtime123063.persistence;

import it.unicam.cs.mpgc.jtime123063.model.Project;
import it.unicam.cs.mpgc.jtime123063.model.SimpleProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimpleProjectXmlParser implements XmlParser<Project> {

    private static final String SECTION_NAME = "projects_store"; // Coerente con tasks_store
    private static final String ELEMENT_NAME = "project";
    private static final String ID_ATTRIBUTE = "id";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String CLOSED_ATTRIBUTE = "closed";

    @Override
    public String getSectionName() { return SECTION_NAME; }

    @Override
    public String getElementName() { return ELEMENT_NAME; }

    @Override
    public Project fromXml(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        String name = element.getAttribute(NAME_ATTRIBUTE);

        // Creiamo il progetto semplice.
        // I task NON vengono caricati qui, ma dal Manager che unirà le due liste.
        SimpleProject project = new SimpleProject(id, name);

        if (element.hasAttribute(CLOSED_ATTRIBUTE)) {
            project.setClosed(Boolean.parseBoolean(element.getAttribute(CLOSED_ATTRIBUTE)));
        }

        return project;
    }

    @Override
    public Element toXml(Project project, Document doc) {
        Element projectElement = doc.createElement(ELEMENT_NAME);
        projectElement.setAttribute(ID_ATTRIBUTE, project.getId());
        projectElement.setAttribute(NAME_ATTRIBUTE, project.getName());
        projectElement.setAttribute(CLOSED_ATTRIBUTE, String.valueOf(project.isClosed()));

        // NOTA: Non salviamo i task qui dentro!
        // Sarà la TaskRepository a salvarli nella sua sezione piatta.

        return projectElement;
    }
}