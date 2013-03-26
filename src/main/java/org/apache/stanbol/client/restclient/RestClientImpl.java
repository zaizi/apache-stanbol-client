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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.stanbol.client.restclient.exception.RestClientException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.multipart.BodyPart;

/**
 * Implementation of {@link RestClient}
 * 
 * @author efoncubierta
 * @author Rafa Haro
 * 
 */
public class RestClientImpl implements RestClient
{

    // Properties
    private String endpoint;
    private URL endpointURL;
    private Client httpClient;
    private List<ClientFilter> filters;

    /**
     * @see org.zaizi.restclient.RestClient#getEndpoint()
     */
    @Override
    public String getEndpoint()
    {
        return endpoint;
    }

    /**
     * @see org.apache.stanbol.client.restclient.RestClient#setEndpoint(java.lang.String)
     */
    @Override
    public void setEndpoint(String endpoint) throws MalformedURLException
    {
        endpointURL = new URL(endpoint);
        this.endpoint = endpoint;
    }

    /**
     * @see org.zaizi.restclient.RestClient#getHttpClient()
     */
    @Override
    public Client getHttpClient()
    {
        return httpClient;
    }

    /**
     * @see org.zaizi.restclient.RestClient#getFilters()
     */
    @Override
    public List<ClientFilter> getFilters()
    {
        return filters;
    }

    /**
     * @see org.zaizi.restclient.RestClient#setFilters(java.util.List)
     */
    @Override
    public void setFilters(List<ClientFilter> filters)
    {
        this.filters = filters;
    }

    /**
     * @see org.zaizi.restclient.RestClient#setHttpClient(com.sun.jersey.api.client.Client)
     */
    @Override
    public void setHttpClient(Client httpClient)
    {
        this.httpClient = httpClient;
    }

    /**
     * @see org.zaizi.restclient.RestClient#get(java.lang.String)
     */
    @Override
    public ClientResponse get(String uri)
    {
        return get(uri, MediaType.WILDCARD_TYPE, null);
    }

    /**
     * @see org.zaizi.restclient.RestClient#get(java.lang.String, org.zaizi.restclient.Parameters)
     */
    @Override
    public ClientResponse get(String uri, Parameters parameters)
    {
        return get(uri, MediaType.WILDCARD_TYPE, parameters);
    }

    /**
     * @see org.zaizi.restclient.RestClient#get(java.lang.String, javax.ws.rs.core.MediaType)
     */
    @Override
    public ClientResponse get(String uri, MediaType acceptType)
    {
        return get(uri, acceptType, null);
    }

    /**
     * @see org.zaizi.restclient.RestClient#get(java.lang.String, javax.ws.rs.core.MediaType,
     * org.zaizi.restclient.Parameters)
     */
    @Override
    public ClientResponse get(String uri, MediaType acceptType, Parameters parameters)
    {

        URL url;
        try
        {
            url = buildURL(uri, parameters);
        }
        catch (MalformedURLException e)
        {
            throw new RestClientException("Error building the URL " + e.getMessage(), e);
        }

        httpClient.setFollowRedirects(true);

        WebResource resource = httpClient.resource(url.toString());
        applyFilters(resource);

        ClientResponse response = resource.accept(acceptType).get(ClientResponse.class);
        return response;
    }

    /**
     * @see org.zaizi.restclient.RestClient#post(java.lang.String, java.lang.Object, javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MediaType)
     */
    @Override
    public ClientResponse post(String uri, Object object, MediaType objectType, MediaType acceptType)
    {
        return post(uri, object, objectType, acceptType, null);
    }

    /**
     * @see org.zaizi.restclient.RestClient#post(java.lang.String, java.lang.Object, javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MediaType, org.zaizi.restclient.Parameters)
     */
    @Override
    public ClientResponse post(String uri, Object object, MediaType objectType, MediaType acceptType,
            Parameters parameters)
    {
        URL url;
        try
        {
            url = buildURL(uri, parameters);
        }
        catch (MalformedURLException e)
        {
            throw new RestClientException("Error building the URL " + e.getMessage(), e);
        }

        httpClient.setFollowRedirects(false);

        WebResource resource = httpClient.resource(url.toString());
        applyFilters(resource);

        ClientResponse response = resource.accept(acceptType).entity(object, objectType).post(ClientResponse.class);
        return response;
    }

    /**
     * @see org.apache.stanbol.client.restclient.RestClient#post(java.lang.String, javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MediaType, org.apache.stanbol.client.restclient.Parameters)
     */
    public ClientResponse post(String uri, MediaType objectType, MediaType acceptType, Parameters parameters)
    {
        return post(uri, null, objectType, acceptType, parameters);
    }

    /**
     * @see org.zaizi.restclient.RestClient#post(java.lang.String, java.io.InputStream, javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MediaType)
     */
    @Override
    public ClientResponse post(String uri, InputStream is, MediaType objectType, MediaType acceptType)
    {
        return post(uri, is, objectType, acceptType, null);
    }

    /**
     * @see org.zaizi.restclient.RestClient#post(java.lang.String, java.io.InputStream, javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MediaType, org.zaizi.restclient.Parameters)
     */
    @Override
    public ClientResponse post(String uri, InputStream is, MediaType objectType, MediaType acceptType,
            Parameters parameters)
    {
        URL url;
        try
        {
            if (parameters != null)
            {
                url = buildURL(uri, parameters);
            }
            else
            {
                url = buildURL(uri);
            }
        }
        catch (MalformedURLException e)
        {
            throw new RestClientException("Error building the URL " + e.getMessage(), e);
        }

        httpClient.setFollowRedirects(false);

        WebResource resource = httpClient.resource(url.toString());
        applyFilters(resource);
        ClientResponse response = resource.accept(acceptType).entity(is, objectType).post(ClientResponse.class);
        return response;
    }

    /**
     * @see org.apache.stanbol.client.restclient.RestClient#post(java.lang.String,
     * com.sun.jersey.multipart.FormDataMultiPart, javax.ws.rs.core.MediaType)
     */
    public ClientResponse post(String uri, BodyPart form, MediaType acceptType, Parameters parameters)
    {
        URL url;
        try
        {
            if (parameters != null)
            {
                url = buildURL(uri, parameters);
            }
            else
            {
                url = buildURL(uri);
            }
        }
        catch (MalformedURLException e)
        {
            throw new RestClientException("Error building the URL " + e.getMessage(), e);
        }

        httpClient.setFollowRedirects(false);

        WebResource resource = httpClient.resource(url.toString());
        applyFilters(resource);

        ClientResponse response = resource.accept(acceptType).type(MediaType.MULTIPART_FORM_DATA).post(
                ClientResponse.class, form);

        return response;

    }

    /**
     * @see org.apache.stanbol.client.restclient.RestClient#post(java.lang.String, com.sun.jersey.multipart.BodyPart, org.apache.stanbol.client.restclient.Parameters)
     */
    public ClientResponse post(String uri, BodyPart form, Parameters parameters)
    {
        URL url;
        try
        {
            if (parameters != null)
            {
                url = buildURL(uri, parameters);
            }
            else
            {
                url = buildURL(uri);
            }
        }
        catch (MalformedURLException e)
        {
            throw new RestClientException("Error building the URL " + e.getMessage(), e);
        }

        httpClient.setFollowRedirects(false);

        WebResource resource = httpClient.resource(url.toString());
        applyFilters(resource);

        ClientResponse response = resource.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, form);

        return response;

    }

    /**
     * @see org.zaizi.restclient.RestClient#put(java.lang.String, java.lang.Object, javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MediaType)
     */
    @Override
    public ClientResponse put(String uri, Object object, MediaType objectType, MediaType acceptType)
    {
        URL url;
        try
        {
            url = buildURL(uri);
        }
        catch (MalformedURLException e)
        {
            throw new RestClientException("Error building the URL " + e.getMessage(), e);
        }

        httpClient.setFollowRedirects(false);

        WebResource resource = httpClient.resource(url.toString());
        applyFilters(resource);

        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE).entity(object, acceptType).put(
                ClientResponse.class);
        return response;
    }

    /**
     * @see org.zaizi.restclient.RestClient#put(java.lang.String, java.lang.Object, javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MediaType, org.zaizi.restclient.Parameters)
     */
    @Override
    public ClientResponse put(String uri, Object object, MediaType objectType, MediaType acceptType,
            Parameters parameters)
    {
        URL url;
        try
        {
            url = buildURL(uri, parameters);
        }
        catch (MalformedURLException e)
        {
            throw new RestClientException("Error building the URL " + e.getMessage(), e);
        }

        httpClient.setFollowRedirects(false);

        WebResource resource = httpClient.resource(url.toString());
        applyFilters(resource);

        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE).entity(object, acceptType).put(
                ClientResponse.class);
        return response;
    }

    /**
     * @see org.zaizi.restclient.RestClient#put(java.lang.String, java.io.InputStream, javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MediaType)
     */
    @Override
    public ClientResponse put(String uri, InputStream is, MediaType mimetype, MediaType acceptType)
    {
        return put(uri, is, mimetype, acceptType, null);
    }

    /**
     * @see org.zaizi.restclient.RestClient#put(java.lang.String, java.io.InputStream, javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MediaType, org.zaizi.restclient.Parameters)
     */
    @Override
    public ClientResponse put(String uri, InputStream is, MediaType mimetype, MediaType acceptType,
            Parameters parameters)
    {
        URL url;
        try
        {
            if (parameters != null)
            {
                url = buildURL(uri, parameters);
            }
            else
            {
                url = buildURL(uri);
            }

        }
        catch (MalformedURLException e)
        {
            throw new RestClientException("Error building the URL " + e.getMessage(), e);
        }

        httpClient.setFollowRedirects(false);

        WebResource resource = httpClient.resource(url.toString());
        applyFilters(resource);

        ClientResponse response = resource.accept(MediaType.WILDCARD_TYPE).entity(is, mimetype).put(
                ClientResponse.class);
        return response;
    }

    /**
     * @see org.zaizi.restclient.RestClient#delete(java.lang.String)
     */
    @Override
    public ClientResponse delete(String uri)
    {
        return delete(uri, null);
    }

    /**
     * @see org.zaizi.restclient.RestClient#delete(java.lang.String, org.zaizi.restclient.Parameters)
     */
    @Override
    public ClientResponse delete(String uri, Parameters parameters)
    {
        URL url;
        try
        {
            url = buildURL(uri, parameters);
        }
        catch (MalformedURLException e)
        {
            throw new RestClientException("Error building the URL " + e.getMessage(), e);
        }

        httpClient.setFollowRedirects(true);

        WebResource resource = httpClient.resource(url.toString());
        applyFilters(resource);

        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE).delete(ClientResponse.class);
        return response;
    }

    /**
     * Create the absolute url
     * 
     * @param uri Uri
     * @return Url
     * @throws MalformedURLException
     */
    private URL buildURL(String uri) throws MalformedURLException
    {
        return buildURL(uri, null);
    }

    /**
     * Create the absolute url
     * 
     * @param uri Uri
     * @param parameters Uri parameters
     * @return Url
     * @throws MalformedURLException
     */
    private URL buildURL(String uri, Parameters parameters) throws MalformedURLException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(endpointURL);
        sb.append(uri);

        if (parameters != null)
        {
            if (!uri.contains("?"))
            {
                sb.append("?");
            }
            else
            {
                sb.append("&");
            }

            Boolean first = Boolean.TRUE;
            for (Map.Entry<String, String> parameter : parameters.entrySet())
            {
                try
                {
                    String encodedParameter = URLEncoder.encode(parameter.getValue(), "UTF-8");
                    sb.append(!first ? "&" : "").append(parameter.getKey()).append("=").append(encodedParameter);
                }
                catch (UnsupportedEncodingException e)
                {
                    // TODO manage exception
                }
                first = Boolean.FALSE;
            }
        }
        return new URL(sb.toString());
    }

    /**
     * Apply Jersey filters
     * 
     * @param resource Web resource
     */
    private void applyFilters(WebResource resource)
    {
        if (filters != null)
        {
            for (ClientFilter filter : filters)
            {
                resource.addFilter(filter);
            }
        }
    }
}
