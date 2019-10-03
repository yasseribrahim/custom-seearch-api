/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.google.search;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.CustomsearchRequestInitializer;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author interactive
 */
public final class CustomGoogleSearchEngineImpl implements CustomGoogleSearchEngine {

    public static final String DEFUALT_API_KEY = "AIzaSyCnkwCjkTJX7u9-lAyyuan7V8soh24NPXo";
    public static final String DEFUALT_CX = "009648126117645070809:0jderb5rtvu";
    public static final long DEFUALT_PAGE_SIZE = 10;
    public static final long DEFUALT_TOTAL_RESULTS = 100;

    private Customsearch customsearch;
    private Customsearch.Cse.List list;
    private String cx;
    private List<String> terms;
    private Search search;
    private List<Result> results;
    private long page;
    private long pageSize;
    private long totalResults;

    public CustomGoogleSearchEngineImpl(List<String> terms) throws Exception {
        this(DEFUALT_API_KEY, DEFUALT_CX, DEFUALT_PAGE_SIZE, DEFUALT_TOTAL_RESULTS, terms);
    }

    public CustomGoogleSearchEngineImpl(String apiKey, String cx, long pageSize, long totalResults, List<String> terms) throws Exception {
        setPageSize(pageSize);
        setTotalResults(totalResults);
        this.cx = cx;
        this.terms = terms;
        this.page = 1;
        this.results = new ArrayList<>();

        //Instance Customsearch
        customsearch = new Customsearch.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), null)
                .setApplicationName(getClass().getName())
                .setGoogleClientRequestInitializer(new CustomsearchRequestInitializer(apiKey))
                .build();
        //Set search parameter
        list = customsearch.cse().list(getSearchQuery()).setCx(cx);
    }

    /**
     * 
     * @param page start page from zero
     * @return
     * @throws IOException 
     */
    @Override
    public List<Result> execute(long page) throws IOException {
        setPage(page);
        list.setStart(calculateStart());
        list.setNum(pageSize);
        search = list.execute();
        setPage(++page);
        results.clear();
        if (search.getItems() != null) {
            results.addAll(search.getItems());
        }

        return results;
    }

    @Override
    public List<Result> execute() throws IOException {
        setPage(1);
        return execute(page);
    }

    @Override
    public List<Result> fetchAll() throws IOException {
        List<Result> fetched = new ArrayList<>();
        setPage(0);
        do {
            results = execute(page);
            if (!results.isEmpty()) {
                fetched.addAll(results);
            } else {
                break;
            }
        } while (hasNext());
        return fetched;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public void setPageSize(long pageSize) throws IllegalArgumentException {
        if (pageSize > 0 && pageSize <= DEFUALT_PAGE_SIZE) {
            this.pageSize = pageSize;
        } else {
            throw new IllegalArgumentException("IllegalArgumentException: Page size must be positive number and less than or equal 10");
        }
    }

    public void setTotalResults(long totalResults) {
        if (totalResults > 0 && totalResults <= DEFUALT_TOTAL_RESULTS) {
            this.totalResults = totalResults;
        } else {
            throw new IllegalArgumentException("IllegalArgumentException: Total results must be positive number and less than or equal 100");
        }
    }

    @Override
    public boolean hasNext() throws IllegalStateException {
        if (search != null) {
            long nextTotal = calculateStart() - 1 + pageSize;
            return (nextTotal <= totalResults) && (nextTotal <= search.getSearchInformation().getTotalResults());
        }
        throw new IllegalStateException("Firstly must call execute function");
    }

    public List<Result> getResults() {
        return results;
    }

    private long calculateStart() {
        return (page * pageSize) + 1;
    }

    private String getSearchQuery() {
        StringBuilder builder = new StringBuilder();
        for (String term : terms) {
            builder.append(term).append(" ");
        }
        return builder.toString().trim();
    }
}
