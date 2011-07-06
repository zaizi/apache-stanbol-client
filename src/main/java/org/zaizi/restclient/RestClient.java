package org.zaizi.restclient;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;


/**
 * Define a REST client
 * 
 * @author efoncubierta
 *
 */
public interface RestClient {
	
	/**
	 * Set the service enpoint
	 * 
	 * @param endpoint Service endpoint
	 */
	public void setEndpoint(Endpoint endpoint);
	
	/**
	 * Get the service endpoint
	 * 
	 * @return Service endpoint
	 */
	public Endpoint getEndpoint();
	
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
	 * @param object Object to be serialized
	 * @param objectType Object content type
	 * @param acceptType Content type accepted
	 * @param parameters URI parameters
	 * @return Service response
	 */
    public ClientResponse post(String uri, Object object, MediaType objectType, MediaType acceptType, Parameters parameters);
    
	/**
	 * Do a 'post' call
	 * 
	 * @param uri Service URI
	 * @param is Input stream
	 * @param mimetype Input mimetype
	 * @param acceptType Content type accepted
	 * @return Service response
	 */
    public ClientResponse post(String uri, InputStream is, MediaType mimetype, MediaType acceptType);
    
    /**
	 * Do a 'post' call
	 * 
	 * @param uri Service URI
	 * @param is Input stream
	 * @param mimetype Input mimetype
	 * @param acceptType Content type accepted
	 * @param parameters URI parameters
	 * @return Service response
	 */
    public ClientResponse post(String uri, InputStream is, MediaType mimetype, MediaType acceptType, Parameters parameters);
    
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
    public ClientResponse put(String uri, Object object, MediaType objectType, MediaType acceptType, Parameters parameters);

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
    public ClientResponse put(String uri, InputStream is, MediaType mimetype, MediaType acceptType, Parameters parameters);

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