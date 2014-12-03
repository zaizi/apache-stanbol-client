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
import javax.ws.rs.core.UriBuilder;

import org.apache.stanbol.client.Sparql;
import org.apache.stanbol.client.rest.RestClientExecutor;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Stanbol Sparql Endpoint Client Implementation
 * 
 * @author Rafa Haro <rharo@zaizi.com>
 *
 */
public class SparqlImpl implements Sparql
{
    private Logger logger = LoggerFactory.getLogger(SparqlImpl.class);

    private UriBuilder builder;
    
    /**
     * Constructor
     * 
     * @param restClient REST Client
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
        try{
        	String response = RestClientExecutor.get(uri,
        			new MediaType("application", "sparql-results+xml"), String.class);
        	
        	if (logger.isDebugEnabled())
            {
                logger.debug("SPARQL query sucessfully executed through " + graph + " graph at Stanbol Server");
            }
        	
        	return ResultSetFactory.fromXML(response);
        	
        }catch(UniformInterfaceException e){
        	ClientResponse response = e.getResponse();
            
            // Check HTTP status code
            int status = response.getStatus();
            if (status == 404){
            	throw new StanbolServiceException("Stanbol Server unrecheable");
            }

            if (status != 200 && status != 201 && status != 202)
            {
            	if(logger.isTraceEnabled()){
            		String trace = response.getEntity(String.class);
            		logger.trace(trace);
            	}
            }
            
            throw new StanbolServiceException("[HTTP " + status + "] Error executing the following SPARQL query in stanbol server:\n" + sparqlQuery);
        }catch(ClientHandlerException e){
        	String message = "Error Parsing SPARQL Service Response";
        	if(logger.isTraceEnabled())
        		logger.trace(message, e);
        	throw new StanbolServiceException(message);
        }
    }
}
