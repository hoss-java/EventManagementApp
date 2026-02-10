package com.EventManApp;

import com.EventManApp.KVSubject;
import com.EventManApp.KVSubjectHandlerInterface;
import com.EventManApp.KVSubjectStorage;
import com.EventManApp.lib.DebugUtil;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class KVSubjectHandler implements KVSubjectHandlerInterface {
    private KVSubjectStorage kvSubjectStorage;

    public KVSubjectHandler(String xmlFilePath, KVSubjectStorage storage) {
        this.kvSubjectStorage = storage; // Initialize using injected storage
        if (this.kvSubjectStorage.countKVSubjects() == 0) {
            loadDataFromXML(xmlFilePath);
        }
    }

    private void loadDataFromXML(String xmlFilePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + xmlFilePath);
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList subjectNodes = document.getElementsByTagName("subject");
            for (int i = 0; i < subjectNodes.getLength(); i++) {
                Element subjectElement = (Element) subjectNodes.item(i);
                String identifier = subjectElement.getAttribute("identifier");
                addKVSubject(identifier, subjectElement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addKVSubject(String identifier, Element subjectElement) {
        // Check if the storage already contains the identifier
        if (kvSubjectStorage.getKVSubject(identifier) == null) {
            KVSubject subject = new KVSubject(identifier);
            initializeFieldTypeMap(subject, subjectElement);
            kvSubjectStorage.addKVSubject(subject); // Use storage method to add
        }
    }

    @Override
    public void removeKVSubject(String identifier) {
        KVSubject subject = kvSubjectStorage.getKVSubject(identifier);
        if (subject != null) {
            kvSubjectStorage.removeKVSubject(subject); // Use storage method to remove
        }
    }

    @Override
    public KVSubject getKVSubject(String identifier) {
        return kvSubjectStorage.getKVSubject(identifier); // Use storage method to get
    }

    // New method to get fieldTypeMap for a given identifier
    public Map<String, KVObjectField> getFieldTypeMapByIdentifier(String identifier) {
        KVSubject subject = getKVSubject(identifier);
        return (subject != null) ? subject.getFieldTypeMap() : null;
    }
    
    private void initializeFieldTypeMap(KVSubject subject, Element subjectElement) {
        NodeList fieldNodes = subjectElement.getElementsByTagName("field");
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Element fieldElement = (Element) fieldNodes.item(i);
            String field = fieldElement.getAttribute("field");
            String name = fieldElement.getAttribute("name");
            String type = fieldElement.getAttribute("type");
            boolean mandatory = Boolean.parseBoolean(fieldElement.getAttribute("mandatory"));
            String modifier = fieldElement.getAttribute("modifier");
            String defaultValue = fieldElement.getAttribute("defaultValue");

            subject.getFieldTypeMap().put(name, new KVObjectField(field,type, mandatory, modifier, defaultValue));
        }
    }
}
