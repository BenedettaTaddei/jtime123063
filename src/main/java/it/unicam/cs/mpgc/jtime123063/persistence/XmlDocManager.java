package it.unicam.cs.mpgc.jtime123063.persistence;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Path;

/**
 * Gestisce il caricamento e il salvataggio dei documenti XML per JTime.
 */
public class XmlDocManager {
    private final Path xmlFilePath;

    /**
     * Costruttore predefinito che punta a data/jtime.xml.
     */
    public XmlDocManager() {
        // Ho cambiato il nome del file in jtime.xml
        this.xmlFilePath = Path.of("data/jtime.xml");
        ensureDataDirectoryExists();
    }

    public XmlDocManager(Path xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
        ensureDataDirectoryExists();
    }

    /**
     * Assicura che la cartella 'data' esista per evitare FileNotFoundException.
     */
    private void ensureDataDirectoryExists() {
        File dir = xmlFilePath.getParent().toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public Document loadDocument() throws Exception {
        File xmlFile = xmlFilePath.toFile();
        if (!xmlFile.exists()) {
            return createNewDocument();
        }
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(xmlFile);
    }

    public void saveDocument(Document doc) throws Exception {
        doc.normalizeDocument();
        removeEmptyTextNodes(doc);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        transformer.transform(new DOMSource(doc), new StreamResult(xmlFilePath.toFile()));
    }

    /**
     * Crea un nuovo documento con l'elemento radice corretto per JTime.
     */
    public Document createNewDocument() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        // Elemento radice specifico per il nuovo progetto
        doc.appendChild(doc.createElement("jtime"));
        return doc;
    }

    private void removeEmptyTextNodes(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.hasChildNodes()) {
                removeEmptyTextNodes(child);
            }
        }
    }
}