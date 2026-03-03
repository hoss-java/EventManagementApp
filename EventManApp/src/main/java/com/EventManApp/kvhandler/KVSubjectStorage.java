package com.EventManApp.kvhandler;

import java.util.List;

import com.EventManApp.kvhandler.KVSubject;

public interface KVSubjectStorage {
    void addKVSubject(KVSubject kvSubject);
    void updateKVSubject(KVSubject kvSubject);
    boolean removeKVSubject(KVSubject kvSubject);
    KVSubject getKVSubject(String identifier);
    List<KVSubject> getAllKVSubjects();
    int countKVSubjects();
    void close();
}
