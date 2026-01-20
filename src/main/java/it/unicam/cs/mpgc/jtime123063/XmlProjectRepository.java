package it.unicam.cs.mpgc.jtime123063;
import org.w3c.dom.*;
import java.util.*;

public class XmlProjectRepository implements ProjectRepository {
    private final XmlDocManager docManager = new XmlDocManager();
    private final SimpleProjectXmlParser parser = new SimpleProjectXmlParser();

    @Override
    public void saveAll(List<Project> projects) {
        try {
            Document doc = docManager.createNewDocument(); // Crea un nuovo doc pulito
            Element root = doc.createElement("projects");
            doc.appendChild(root);

            for (Project p : projects) {
                root.appendChild(parser.toXml(p, doc));
            }
            docManager.saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Project> loadAll() {
        List<Project> projects = new ArrayList<>();
        try {
            Document doc = docManager.loadDocument();
            NodeList projectNodes = doc.getElementsByTagName("project");

            for (int i = 0; i < projectNodes.getLength(); i++) {
                projects.add(parser.fromXml((Element) projectNodes.item(i)));
            }
        } catch (Exception e) {
            // Se il file non esiste, ritorna lista vuota
        }
        return projects;
    }
}