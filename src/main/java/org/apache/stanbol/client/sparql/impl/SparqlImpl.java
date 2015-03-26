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
package org.apache.stanbol.client.sparql.impl;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.stanbol.client.Sparql;
import org.apache.stanbol.client.rest.RestClientExecutor;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;

/**
 * Stanbol Sparql Endpoint Client Implementation
 * 
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 *
 */
public class SparqlImpl implements Sparql
{
    private Logger logger = LoggerFactory.getLogger(SparqlImpl.class);

    private UriBuilder builder;
    
    /**
     * Constructor
     * 
     */
    public SparqlImpl(UriBuilder builder)
    {
        this.builder = builder;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.stanbol.client.Sparql#executeQuery(java.lang.String, java.lang.String)
     */
    @Override
    public ResultSet executeQuery(String graphUri, String sparqlQuery) throws StanbolServiceException
    {
    	String graph = ENHANCEMENT_GRAPH_URI;
    	if(graphUri != null && !graphUri.isEmpty())
    		graph = graphUri;
    	
    	UriBuilder sparqlBuilder = 
    			builder.clone().queryParam("graphuri", graph);
    	     
        if(sparqlQuery != null && !sparqlQuery.equals(""))
        	sparqlBuilder = sparqlBuilder.queryParam("query", sparqlQuery);
        
        URI uri = sparqlBuilder.build();
        Response response = RestClientExecutor.get(uri, new MediaType("application", "sparql-results+xml"));
       
        // Check HTTP status code
        int status = response.getStatus();
        if (status == 404){
        	String stackTrace = response.readEntity(String.class);
        	logger.error(stackTrace);
            return null;
        }

        if (status != 200 && status != 201 && status != 202)
        {
            throw new StanbolServiceException("[HTTP " + status + "] Error executing the following SPARQL query in stanbol server:\n" + sparqlQuery);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("SPARQL query sucessfully executed through " + graph + " graph at Stanbol Server");
        }

        return ResultSetFactory.fromXML(response.readEntity(String.class));
    }

}
