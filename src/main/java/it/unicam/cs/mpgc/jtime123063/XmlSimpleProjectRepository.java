package it.unicam.cs.mpgc.jtime123063;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione specifica per il salvataggio in formato XML dei SimpleProject.
 * Utilizza il parser per gestire la traduzione e mantiene la struttura piatta.
 */
public class XmlSimpleProjectRepository implements ProjectRepository {

    private final XmlDocManager xmlDocManager = new XmlDocManager();
    private final XmlSectionExtractor sectionExtractor = new XmlSectionExtractor();
    private final XmlParser<Project> parser = new SimpleProjectXmlParser();

    @Override
    public void save(Project project) {
        if (project == null) return;
        try {
            // 1. Rimuoviamo la versione precedente (se esiste) per gestire l'aggiornamento
            remove(project);

            // 2. Carichiamo il documento e aggiungiamo la nuova versione
            Document doc = xmlDocManager.loadDocument();
            Element section = sectionExtractor.getOrCreateSection(doc, parser.getSectionName());

            section.appendChild(parser.toXml(project, doc));
            xmlDocManager.saveDocument(doc);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio del progetto: " + e.getMessage());
        }
    }

    @Override
    public boolean remove(Project project) {
        if (project == null) return false;
        try {
            Document doc = xmlDocManager.loadDocument();
            Element section = sectionExtractor.getOrCreateSection(doc, parser.getSectionName());
            NodeList nodes = section.getElementsByTagName(parser.getElementName());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element current = (Element) nodes.item(i);
                // Confronto tramite l'ID univoco
                if (current.getAttribute("id").equals(project.getId())) {
                    section.removeChild(current);
                    xmlDocManager.saveDocument(doc);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Errore nella rimozione del progetto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        try {
            // Usa il sectionExtractor per recuperare i nodi basandosi sui nomi del parser
            NodeList projectNodes = sectionExtractor.getElementsInSection(
                    parser.getSectionName(),
                    parser.getElementName()
            );

            for (int i = 0; i < projectNodes.getLength(); i++) {
                Element projectElement = (Element) projectNodes.item(i);
                projects.add(parser.fromXml(projectElement));
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dei progetti: " + e.getMessage());
        }
        return projects;
    }

    @Override
    public void clearAll() {
        try {
            Document doc = xmlDocManager.loadDocument();
            Element section = sectionExtractor.getOrCreateSection(doc, parser.getSectionName());

            while (section.hasChildNodes()) {
                section.removeChild(section.getFirstChild());
            }

            xmlDocManager.saveDocument(doc);
        } catch (Exception e) {
            System.err.println("Errore nella pulizia della sezione progetti: " + e.getMessage());
        }
    }
}