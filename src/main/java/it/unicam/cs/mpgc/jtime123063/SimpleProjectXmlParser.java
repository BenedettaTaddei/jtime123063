package it.unicam.cs.mpgc.jtime123063;
import org.w3c.dom.*;
import java.time.LocalDate;

public class SimpleProjectXmlParser {
    private static final String PROJECT_TAG = "project";
    private static final String TASK_TAG = "task";

    public Element toXml(Project project, Document doc) {
        Element pElem = doc.createElement(PROJECT_TAG);
        pElem.setAttribute("id", project.getId());
        pElem.setAttribute("name", project.getName());
        pElem.setAttribute("closed", String.valueOf(project.isClosed()));

        for (Task task : project.getTasks()) {
            Element tElem = doc.createElement(TASK_TAG);
            tElem.setAttribute("id", task.getId());
            tElem.setAttribute("title", task.getTitle());
            tElem.setAttribute("estTime", String.valueOf(task.getEstimatedTime()));
            tElem.setAttribute("effTime", String.valueOf(task.getEffectiveTime()));
            tElem.setAttribute("completed", String.valueOf(task.isCompleted()));
            if (task.getScheduledDate() != null) {
                tElem.setAttribute("date", task.getScheduledDate().toString());
            }
            pElem.appendChild(tElem);
        }
        return pElem;
    }

    public Project fromXml(Element pElem) {
        SimpleProject project = new SimpleProject(pElem.getAttribute("name"));
        project.setClosed(Boolean.parseBoolean(pElem.getAttribute("closed")));

        // Caricamento dei Task interni
        NodeList tasks = pElem.getElementsByTagName(TASK_TAG);
        for (int i = 0; i < tasks.getLength(); i++) {
            Element tElem = (Element) tasks.item(i);
            SimpleTask task = new SimpleTask(tElem.getAttribute("title"),
                    Double.parseDouble(tElem.getAttribute("estTime")));
            task.setEffectiveTime(Double.parseDouble(tElem.getAttribute("effTime")));
            task.setCompleted(Boolean.parseBoolean(tElem.getAttribute("completed")));

            if (tElem.hasAttribute("date")) {
                task.setScheduledDate(LocalDate.parse(tElem.getAttribute("date")));
            }
            project.addTask(task);
        }
        return project;
    }
}