package it.unicam.cs.mpgc.jtime123063;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.time.LocalDate;

public class SimpleTaskXmlParser {
    private static final String ELEMENT_NAME = "task";
    private static final String TITLE_ATTR = "title";
    private static final String EST_TIME_ATTR = "estimatedTime";
    private static final String EFF_TIME_ATTR = "effectiveTime";
    private static final String COMPLETED_ATTR = "completed";
    private static final String DATE_ATTR = "scheduledDate";

    public Task fromXml(Element element) {
        String title = element.getAttribute(TITLE_ATTR);
        double estTime = Double.parseDouble(element.getAttribute(EST_TIME_ATTR));

        SimpleTask task = new SimpleTask(title, estTime);
        task.setEffectiveTime(Double.parseDouble(element.getAttribute(EFF_TIME_ATTR)));
        task.setCompleted(Boolean.parseBoolean(element.getAttribute(COMPLETED_ATTR)));

        if (element.hasAttribute(DATE_ATTR) && !element.getAttribute(DATE_ATTR).isEmpty()) {
            task.setScheduledDate(LocalDate.parse(element.getAttribute(DATE_ATTR)));
        }
        return task;
    }

    public Element toXml(Task task, Document doc) {
        Element el = doc.createElement(ELEMENT_NAME);
        el.setAttribute(TITLE_ATTR, task.getTitle());
        el.setAttribute(EST_TIME_ATTR, String.valueOf(task.getEstimatedTime()));
        el.setAttribute(EFF_TIME_ATTR, String.valueOf(task.getEffectiveTime()));
        el.setAttribute(COMPLETED_ATTR, String.valueOf(task.isCompleted()));
        if (task.getScheduledDate() != null) {
            el.setAttribute(DATE_ATTR, task.getScheduledDate().toString());
        }
        return el;
    }
}