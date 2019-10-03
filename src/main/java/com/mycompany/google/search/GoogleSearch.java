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
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author interactive
 */
public class GoogleSearch {

    public static void main(String[] args) throws Exception {
        CustomGoogleSearchEngine engine = new CustomGoogleSearchEngineImpl("AIzaSyBoWMzTS0XpquAmgZbdptebaayKUGmSXFQ",CustomGoogleSearchEngineImpl.DEFUALT_CX,10,100, Arrays.asList("\"dsfs dsfdfsd\""));
        
        List<Result> results = engine.fetchAll();
        for (Result result : results) {
            System.out.println(result);
        }
        System.out.println("Size: " + results.size());
    }

    private static void doSearch() throws Exception {
        String searchQuery = "Java"; //The query to search
        String cx = "002845322276752338984:vxqzfa86nqc"; //Your search engine
        cx = "009648126117645070809:0jderb5rtvu";

        //Instance Customsearch
        Customsearch cs = new Customsearch.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), null)
                .setApplicationName("MyApplication")
                .setGoogleClientRequestInitializer(new CustomsearchRequestInitializer(""))
                .build();

        //Set search parameter
        Customsearch.Cse.List list = cs.cse().list(searchQuery).setCx(cx);

        //Execute search
        long page = 1;
        long size = 10;
        list.setStart(page);
        list.setNum(size);
        Search result = list.execute();
        System.out.println(result.getSearchInformation());
        System.out.println("---------------------------");

        int i = 1;
        List<String> links = new ArrayList<>();
        do {
            if (result.getItems() != null) {
                for (Result ri : result.getItems()) {
                    if (!links.contains(ri.getLink())) {
                        System.out.println(i++ + "-" + ri.getLink());
                        links.add(ri.getLink());
                    }
                }
            } else {
                break;
            }
            page++;
            list.setStart((page * size) + 1);
            System.out.println("\t" + list.getStart());
            result = list.execute();
        } while (page < 10);
    }
}
