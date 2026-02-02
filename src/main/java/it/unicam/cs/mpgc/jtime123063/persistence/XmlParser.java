package it.unicam.cs.mpgc.jtime123063.persistence;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interfaccia generica per la conversione tra elementi XML e oggetti del modello.
 * @param <T> il tipo dell'oggetto del modello (es. Project o Task)
 */
public interface XmlParser<T> {
    T fromXml(Element el);
    Element toXml(T obj, Document doc);
    String getSectionName();
    String getElementName();
}