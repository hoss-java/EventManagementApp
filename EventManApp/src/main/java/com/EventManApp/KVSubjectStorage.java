package com.EventManApp;

import java.util.List;

import com.EventManApp.KVSubject;

public interface KVSubjectStorage {
    void addKVSubject(KVSubject kvSubject);
    boolean removeKVSubject(KVSubject kvSubject);
    KVSubject getKVSubject(String identifier);
    List<KVSubject> getAllKVSubjects();
    int countKVSubjects();
    void close();
}
