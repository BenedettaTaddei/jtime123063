package it.unicam.cs.mpgc.jtime123063;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Classe che si occupa della conversione tra oggetti Project ed elementi XML.
 * Segue la struttura stratificata del progetto JBudget.
 */
public class SimpleProjectXmlParser implements XmlParser<Project> {

    private static final String SECTION_NAME = "projects";
    private static final String ELEMENT_NAME = "project";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String CLOSED_ATTRIBUTE = "closed";
    private static final String TASK_TAG = "task";

    @Override
    public String getSectionName() {
        return SECTION_NAME;
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * Converte un elemento XML in un oggetto Project.
     * I task interni vengono gestiti tramite il SimpleTaskXmlParser.
     */
    @Override
    public Project fromXml(Element element) {
        String name = element.getAttribute(NAME_ATTRIBUTE);
        SimpleProject project = new SimpleProject(name);

        if (element.hasAttribute(CLOSED_ATTRIBUTE)) {
            project.setClosed(Boolean.parseBoolean(element.getAttribute(CLOSED_ATTRIBUTE)));
        }

        // Legge i task associati se esistono (come facevi per il budget)
        NodeList taskNodes = element.getElementsByTagName(TASK_TAG);
        if (taskNodes.getLength() > 0) {
            SimpleTaskXmlParser taskParser = new SimpleTaskXmlParser();
            for (int i = 0; i < taskNodes.getLength(); i++) {
                Element taskElement = (Element) taskNodes.item(i);
                project.addTask(taskParser.fromXml(taskElement));
            }
        }

        return project;
    }

    /**
     * Converte un oggetto Project in un elemento XML.
     */
    @Override
    public Element toXml(Project project, Document doc) {
        Element projectElement = doc.createElement(ELEMENT_NAME);
        projectElement.setAttribute(NAME_ATTRIBUTE, project.getName());
        projectElement.setAttribute(CLOSED_ATTRIBUTE, String.valueOf(project.isClosed()));

        // Aggiunge i task (come facevi con l'elemento budget)
        SimpleTaskXmlParser taskParser = new SimpleTaskXmlParser();
        for (Task task : project.getTasks()) {
            projectElement.appendChild(taskParser.toXml(task, doc));
        }

        return projectElement;
    }
}