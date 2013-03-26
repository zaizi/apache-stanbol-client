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
package org.apache.stanbol.client.sparql.services.impl;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.stanbol.client.restclient.Parameters;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.apache.stanbol.client.services.impl.StanbolServiceAbstract;
import org.apache.stanbol.client.sparql.services.StanbolSparqlService;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author rharo
 *
 */
public class StanbolSparqlServiceImpl  extends StanbolServiceAbstract implements StanbolSparqlService
{
    private Logger logger = Logger.getLogger(StanbolSparqlServiceImpl.class);

    /**
     * Constructor
     * 
     * @param restClient REST Client
     */
    public StanbolSparqlServiceImpl(RestClient restClient)
    {
        super(restClient);
    }

    /**
     * @see org.apache.stanbol.client.sparql.services.StanbolSparqlService#executeQuery(java.lang.String, java.lang.String)
     */
    @Override
    public ResultSet executeQuery(String graphUri, String sparqlQuery) throws StanbolServiceException
    {
        Parameters par = new Parameters();
        if(graphUri == null || graphUri.equals(""))
            par.put("graphuri", ENHANCEMENT_GRAPH_URI);
        else
            par.put("graphuri", graphUri);
        
        if(sparqlQuery != null && !sparqlQuery.equals(""))
            par.put("query", sparqlQuery);

        ClientResponse response = getRestClient().get(STANBOL_SPARQL_PATH, new MediaType("application", "sparql-results+xml"), par);

        // Check HTTP status code
        int status = response.getStatus();
        if (status == 404)
            return null;

        if (status != 200 && status != 201 && status != 202)
        {
            throw new StanbolServiceException("[HTTP " + status + "] Error executing the following SPARQL query in stanbol server:\n" + sparqlQuery);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("SPARQL query sucessfully executed through " + par.get("graphuri") + " graph at Stanbol Server");
        }

        return ResultSetFactory.fromXML(response.getEntityInputStream());
    }

}
