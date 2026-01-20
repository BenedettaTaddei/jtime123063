package it.unicam.cs.mpgc.jtime123063;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Fornisce metodi di utilità per estrarre e gestire sezioni specifiche nei documenti XML di JTime.
 * Permette di organizzare il file XML in blocchi logici (es. la sezione "projects").
 */
public class XmlSectionExtractor {

    private final XmlDocManager xmlManager = new XmlDocManager();

    /**
     * Recupera o crea una sezione nel documento XML dato.
     *
     * @param doc         il Documento XML su cui operare
     * @param sectionName il nome della sezione da recuperare o creare
     * @return l'Element che rappresenta la sezione
     */
    public Element getOrCreateSection(Document doc, String sectionName) {
        NodeList nodes = doc.getDocumentElement().getElementsByTagName(sectionName);

        if (nodes.getLength() > 0) return (Element) nodes.item(0);

        Element newSection = doc.createElement(sectionName);
        doc.getDocumentElement().appendChild(newSection);
        return newSection;
    }

    /**
     * Recupera o crea una sezione utilizzando un documento caricato internamente.
     *
     * @param sectionName il nome della sezione da recuperare o creare
     * @return l'Element che rappresenta la sezione
     * @throws Exception se si verifica un errore durante l'accesso al documento XML
     */
    public Element getOrCreateSection(String sectionName) throws Exception {
        Document doc = getDocument();
        return getOrCreateSection(doc, sectionName);
    }

    /**
     * Recupera tutti gli elementi con il nome specificato all'interno di una data sezione.
     *
     * @param sectionName il nome della sezione in cui cercare
     * @param elementName il nome degli elementi da recuperare
     * @return un NodeList contenente tutti gli elementi corrispondenti
     * @throws Exception se si verifica un errore durante l'accesso al documento XML
     */
    public NodeList getElementsInSection(String sectionName, String elementName) throws Exception {
        Element section = getOrCreateSection(sectionName);
        return section.getElementsByTagName(elementName);
    }

    /**
     * Recupera il documento XML gestito da questo estrattore.
     *
     * @return il Documento XML
     * @throws Exception se si verifica un errore durante il caricamento del documento
     */
    public Document getDocument() throws Exception {
        return xmlManager.loadDocument();
    }
}