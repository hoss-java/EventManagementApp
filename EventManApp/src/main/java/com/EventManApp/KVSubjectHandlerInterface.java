package com.EventManApp;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
public interface KVSubjectHandlerInterface {
    void addKVSubject(String identifier, Element subjectElement);
    void removeKVSubject(String identifier);
    KVSubject getKVSubject(String identifier);
}
