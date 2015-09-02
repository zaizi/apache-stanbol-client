/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.client;

import org.apache.stanbol.client.services.exception.StanbolServiceException;

import com.hp.hpl.jena.query.ResultSet;

/**
 *  
 * @author Rafa Haro <rharo@zaizi.com>
 *
 */
public interface Sparql
{
    /**
     * Default Enhancement Graph Registered in Stanbol 
     */
    public static final String ENHANCEMENT_GRAPH_URI = "org.apache.stanbol.contenthub.enhancements";
    
    /**
     * Service's RESTful Path
     */
    static final String STANBOL_SPARQL_PATH = "sparql/";
    
    /**
     * Execute <code>sparqlQuery</code> query over <code>graphUri</code> graph in Stanbol
     * 
     * @param graphUri Uri of the registered graph in Stanbol
     * @param sparqlQuery SPARQL query
     * @return Jena SPARQL {@link ResultSet}
     * @throws StanbolServiceException 
     */
    ResultSet executeQuery(String graphUri, String sparqlQuery) throws StanbolServiceException;
}
