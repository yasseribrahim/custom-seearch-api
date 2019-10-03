/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.google.search;

import com.google.api.services.customsearch.model.Result;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author interactive
 */
public interface CustomGoogleSearchEngine {

    List<Result> execute(long page) throws IOException;

    List<Result> execute() throws IOException;

    List<Result> fetchAll() throws IOException;

    boolean hasNext();
}
