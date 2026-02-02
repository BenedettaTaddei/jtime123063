package it.unicam.cs.mpgc.jtime123063.persistence;

import it.unicam.cs.mpgc.jtime123063.model.SimpleTask;
import it.unicam.cs.mpgc.jtime123063.model.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.time.LocalDate;

/**
 * Parser XML per la classe SimpleTask.
 * Gestisce la traduzione tra oggetti Java e nodi XML, inclusi i riferimenti ai progetti.
 */
public class SimpleTaskXmlParser implements XmlParser<Task> {

    private static final String SECTION_NAME = "tasks_store";
    private static final String ELEMENT_NAME = "task";

    private static final String ID_ATTR = "id";
    private static final String TITLE_ATTR = "title";
    private static final String EST_TIME_ATTR = "estimatedTime";
    private static final String EFF_TIME_ATTR = "effectiveTime";
    private static final String COMPLETED_ATTR = "completed";
    private static final String DATE_ATTR = "scheduledDate";
    private static final String PROJECT_REF_ATTR = "project_id";

    @Override
    public String getSectionName() { return SECTION_NAME; }

    @Override
    public String getElementName() { return ELEMENT_NAME; }

    @Override
    public Task fromXml(Element element) {
        String id = element.getAttribute(ID_ATTR);
        String title = element.getAttribute(TITLE_ATTR);
        double estTime = Double.parseDouble(element.getAttribute(EST_TIME_ATTR));
        double effTime = Double.parseDouble(element.getAttribute(EFF_TIME_ATTR));
        boolean completed = Boolean.parseBoolean(element.getAttribute(COMPLETED_ATTR));

        LocalDate date = null;
        if (element.hasAttribute(DATE_ATTR) && !element.getAttribute(DATE_ATTR).isEmpty()) {
            date = LocalDate.parse(element.getAttribute(DATE_ATTR));
        } else {
            date = LocalDate.now(); // Default se mancante
        }

        SimpleTask task = new SimpleTask(id, title, estTime, effTime, completed, date);

        return task;
    }

    @Override
    public Element toXml(Task task, Document doc) {
        Element el = doc.createElement(ELEMENT_NAME);
        el.setAttribute(ID_ATTR, task.getId());
        el.setAttribute(TITLE_ATTR, task.getTitle());
        el.setAttribute(EST_TIME_ATTR, String.valueOf(task.getEstimatedTime()));
        el.setAttribute(EFF_TIME_ATTR, String.valueOf(task.getEffectiveTime()));
        el.setAttribute(COMPLETED_ATTR, String.valueOf(task.isCompleted()));

        if (task.getScheduledDate() != null) {
            el.setAttribute(DATE_ATTR, task.getScheduledDate().toString());
        }

        String parentId = (task.getParentProject() != null) ? task.getParentProject().getId() : "";
        el.setAttribute(PROJECT_REF_ATTR, parentId);

        return el;
    }
}