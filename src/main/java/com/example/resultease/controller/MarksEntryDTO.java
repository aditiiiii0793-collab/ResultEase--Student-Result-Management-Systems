package com.example.resultease.controller; // Ensure the package is correct

import com.example.resultease.models.result;
import java.util.ArrayList;
import java.util.List;

public class MarksEntryDTO {

    private List<result> results = new ArrayList<>();

    // Getter
    public List<result> getResults() {
        return results;
    }

    // Setter
    public void setResults(List<result> results) {
        this.results = results;
    }
}