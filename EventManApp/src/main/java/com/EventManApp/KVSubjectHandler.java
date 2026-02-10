package com.EventManApp;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.EventManApp.KVSubjectHandlerInterface;
import com.EventManApp.KVSubject;
import com.EventManApp.lib.DebugUtil;

public class KVSubjectHandler implements KVSubjectHandlerInterface {
    private Map<String, KVSubject> subjects = new HashMap<>();

    public KVSubjectHandler(String xmlFilePath) {
        loadDataFromXML(xmlFilePath);
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
        if (!subjects.containsKey(identifier)) {
            KVSubject subject = new KVSubject(identifier);
            initializeFieldTypeMap(subject, subjectElement); // Pass the XML element to initialize fields
            subjects.put(identifier, subject);
        }
    }

    @Override
    public void removeKVSubject(String identifier) {
        subjects.remove(identifier);
    }

    @Override
    public KVSubject getKVSubject(String identifier) {
        return subjects.get(identifier);
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
