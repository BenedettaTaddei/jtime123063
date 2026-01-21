package it.unicam.cs.mpgc.jtime123063;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;

public class XmlSimpleTaskRepository implements TaskRepository {

    private final XmlDocManager xmlDocManager = new XmlDocManager();
    private final XmlSectionExtractor sectionExtractor = new XmlSectionExtractor();

    // Usiamo il parser come "esperto" della struttura
    private final XmlParser<Task> taskParser = new SimpleTaskXmlParser();

    @Override
    public void save(Task task) {
        if (task == null) return;
        try {
            // Rimuoviamo la versione precedente per l'aggiornamento (stile JBudget)
            remove(task);

            Document doc = xmlDocManager.loadDocument();
            // Chiediamo al parser il nome della sezione
            Element section = sectionExtractor.getOrCreateSection(doc, taskParser.getSectionName());

            section.appendChild(taskParser.toXml(task, doc));
            xmlDocManager.saveDocument(doc);
        } catch (Exception e) {
            System.err.println("Errore SAVE: " + e.getMessage());
        }
    }

    @Override
    public boolean remove(Task task) {
        if (task == null) return false;
        try {
            Document doc = xmlDocManager.loadDocument();
            Element section = sectionExtractor.getOrCreateSection(doc, taskParser.getSectionName());
            NodeList nodes = section.getElementsByTagName(taskParser.getElementName());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                if (el.getAttribute("id").equals(task.getId())) {
                    section.removeChild(el);
                    xmlDocManager.saveDocument(doc);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Errore REMOVE: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Task> findAll() {
        List<Task> allTasks = new ArrayList<>();
        try {
            // Recuperiamo i nodi usando i nomi forniti dal parser
            NodeList nodes = sectionExtractor.getElementsInSection(
                    taskParser.getSectionName(),
                    taskParser.getElementName()
            );

            for (int i = 0; i < nodes.getLength(); i++) {
                allTasks.add(taskParser.fromXml((Element) nodes.item(i)));
            }
        } catch (Exception e) {
            System.err.println("Errore FIND_ALL: " + e.getMessage());
        }
        return allTasks;
    }

    @Override
    public void clearAll() {
        try {
            Document doc = xmlDocManager.loadDocument();
            Element section = sectionExtractor.getOrCreateSection(doc, taskParser.getSectionName());
            while (section.hasChildNodes()) {
                section.removeChild(section.getFirstChild());
            }
            xmlDocManager.saveDocument(doc);
        } catch (Exception e) {
            System.err.println("Errore CLEAR_ALL: " + e.getMessage());
        }
    }
}