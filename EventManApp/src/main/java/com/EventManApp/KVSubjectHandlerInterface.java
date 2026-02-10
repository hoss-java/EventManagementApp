package com.EventManApp;

import org.w3c.dom.Element;

public interface KVSubjectHandlerInterface {
    void addKVSubject(String identifier, Element subjectElement);
    void removeKVSubject(String identifier);
    KVSubject getKVSubject(String identifier);
}
