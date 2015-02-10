package ua.pp.condor.searchengine.index;

import ua.pp.condor.searchengine.dto.Document;

import java.util.Set;

public interface ISearchService {

    boolean isEmpty();

    void addDocument(Document doc);

    Set<Integer> search(String query, LogicType logic, int count);
}
