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
package org.apache.stanbol.client.restclient;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.multipart.BodyPart;

/**
 * Define a REST client
 * 
 * @author efoncubierta
 * 
 */
public interface RestClient
{

    /**
     * Set the service enpoint
     * 
     * @param endpoint Service endpoint
     * @throws MalformedURLException
     */
    public void setEndpoint(String endpoint) throws MalformedURLException;

    /**
     * Get the service endpoint
     * 
     * @return Service endpoint
     */
    public String getEndpoint();

    /**
     * Get the HTTP client
     * 
     * @return HTTP client
     */
    public Client getHttpClient();

    /**
     * Set the HTTP client
     * 
     * @param httpClient HTTP client
     */
    public void setHttpClient(Client httpClient);

    /**
     * Get HTTP filters
     * 
     * @return HTTP filters
     */
    public List<ClientFilter> getFilters();

    /**
     * Set HTTP filters
     * 
     * @param filters HTTP filters
     */
    public void setFilters(List<ClientFilter> filters);

    /**
     * Do a 'get' call
     * 
     * @param uri Service URI
     * @return Service response
     */
    public ClientResponse get(String uri);

    /**
     * Do a 'get' call
     * 
     * @param uri Service URI
     * @param parameters URI parameters
     * @return Service response
     */
    public ClientResponse get(String uri, Parameters parameters);

    /**
     * Do a 'get' call
     * 
     * @param uri Service URI
     * @param acceptType Content type accepted
     * @return Service response
     */
    public ClientResponse get(String uri, MediaType acceptType);

    /**
     * Do a 'get' call
     * 
     * @param uri Service URI
     * @param acceptType Content type accepted
     * @param parameters URI parameters
     * @return Service response
     */
    public ClientResponse get(String uri, MediaType acceptType, Parameters parameters);

    /**
     * Do a 'post' call
     * 
     * @param uri Service URI
     * @param object Object to be serialized
     * @param objectType Object content type
     * @param acceptType Content type accepted
     * @return Service response
     */
    public ClientResponse post(String uri, Object object, MediaType objectType, MediaType acceptType);

    /**
     * Do a 'post' call
     * 
     * @param uri Service URI
     * @param objectType Object content type
     * @param acceptType Content type accepted
     * @param parameters URI parameters
     * @return Service response
     */
    public ClientResponse post(String uri, MediaType objectType, MediaType acceptType, Parameters parameters);

    /**
     * Do a 'post' call
     * 
     * @param uri Service URI
     * @param object Object to be serialized
     * @param objectType Object content type
     * @param acceptType Content type accepted
     * @param parameters URI parameters
     * @return Service response
     */
    public ClientResponse post(String uri, Object object, MediaType objectType, MediaType acceptType,
            Parameters parameters);

    /**
     * Do a 'post' call
     * 
     * @param uri Service URI
     * @param is Input stream
     * @param objectType Input mimetype
     * @param acceptType Content type accepted
     * @return Service response
     */
    public ClientResponse post(String uri, InputStream is, MediaType objectType, MediaType acceptType);

    /**
     * Do a MultiPart 'post' call
     * 
     * @param uri Service URI
     * @param form Form Data
     * @param acceptType Content Type Accepted
     * @param parameters URI parameters
     * @return Service Response
     */
    public ClientResponse post(String uri, BodyPart form, MediaType acceptType, Parameters parameters);

    /**
     * Do a MultiPart 'post' call
     * 
     * @param uri Service URI
     * @param form Form Data
     * @param parameters URI parameters
     * @return Service Response
     */
    public ClientResponse post(String uri, BodyPart form, Parameters parameters);

    /**
     * Do a 'post' call
     * 
     * @param uri Service URI
     * @param is Input stream
     * @param objectType Input mimetype
     * @param acceptType Content type accepted
     * @param parameters URI parameters
     * @return Service response
     */
    public ClientResponse post(String uri, InputStream is, MediaType objectType, MediaType acceptType,
            Parameters parameters);

    /**
     * Do a 'put' call
     * 
     * @param uri Service URI
     * @param object Object to be serialized
     * @param objectType Object content type
     * @param acceptType Content type accepted
     * @return Service response
     */
    public ClientResponse put(String uri, Object object, MediaType objectType, MediaType acceptType);

    /**
     * Do a 'put' call
     * 
     * @param uri Service URI
     * @param object Object to be serialized
     * @param objectType Object content type
     * @param acceptType Content type accepted
     * @param parameters URI parameters
     * @return Service response
     */
    public ClientResponse put(String uri, Object object, MediaType objectType, MediaType acceptType,
            Parameters parameters);

    /**
     * Do a 'put' call
     * 
     * @param uri Service URI
     * @param is Input stream
     * @param mimetype Input mimetype
     * @param acceptType Content type accepted
     * @return Service response
     */
    public ClientResponse put(String uri, InputStream is, MediaType mimetype, MediaType acceptType);

    /**
     * Do a 'put' call
     * 
     * @param uri Service URI
     * @param is Input stream
     * @param mimetype Input mimetype
     * @param acceptType Content type accepted
     * @param parameters URI parameters
     * @return Service response
     */
    public ClientResponse put(String uri, InputStream is, MediaType mimetype, MediaType acceptType,
            Parameters parameters);

    /**
     * Do a 'delete' call
     * 
     * @param uri Service URI
     * @return Service response
     */
    public ClientResponse delete(String uri);

    /**
     * Do a 'delete' call
     * 
     * @param uri Service URI
     * @param parameters URI parameters
     * @return Service response
     */
    public ClientResponse delete(String uri, Parameters parameters);
}