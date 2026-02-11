package it.unicam.cs.mpgc.jtime123063.repository;

import it.unicam.cs.mpgc.jtime123063.model.Task;
import it.unicam.cs.mpgc.jtime123063.persistence.SimpleTaskXmlParser;
import it.unicam.cs.mpgc.jtime123063.persistence.XmlDocManager;
import it.unicam.cs.mpgc.jtime123063.persistence.XmlParser;
import it.unicam.cs.mpgc.jtime123063.persistence.XmlSectionExtractor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlSimpleTaskRepository implements TaskRepository {

    private final XmlDocManager xmlDocManager = new XmlDocManager();
    private final XmlSectionExtractor sectionExtractor = new XmlSectionExtractor();
    private final XmlParser<Task> taskParser = new SimpleTaskXmlParser();
    /**
     * Cache temporanea dei metadati di relazione (Task-Project).
     * Necessaria per la ricostruzione del grafo degli oggetti in fase di sincronizzazione,
     * evitando ridondanti accessi al file system.
     */
    private Map<String, String> taskToProjectMap = new HashMap<>();

    @Override
    public void save(Task task) {
        if (task == null) return;
        try {
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
        taskToProjectMap.clear(); // Svuota la mappa vecchia
        try {
            NodeList nodes = sectionExtractor.getElementsInSection(taskParser.getSectionName(), taskParser.getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                // LEGGI L'ID E SALVALO NELLA MAPPA
                String tid = el.getAttribute("id");
                String pid = el.getAttribute("project_id");
                if (!pid.isEmpty()) {
                    taskToProjectMap.put(tid, pid);
                }
                allTasks.add(taskParser.fromXml(el));
            }
        } catch (Exception e) { /* ... */ }
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

    public String getProjectIdForTask(String taskId) {
        if (taskId == null) return null;
        // Deve usare ESATTAMENTE il nome della variabile che hai visto nel debug
        return this.taskToProjectMap.get(taskId);
    }
}