package org.zaizi.restclient;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.zaizi.restclient.exception.RestClientException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * Implementation of {@link RestClient}
 * 
 * @author efoncubierta
 *
 */
public class RestClientImpl implements RestClient {

	// Propiedades
	private Endpoint endpoint;
	private Client httpClient;
	private List<ClientFilter> filters;
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#getEndpoint()
	 */
	@Override
	public Endpoint getEndpoint(){
		return endpoint;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#setEndpoint(org.zaizi.restclient.Endpoint)
	 */
	@Override
	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#getHttpClient()
	 */
	@Override
	public Client getHttpClient() {
		return httpClient;
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#getFilters()
	 */
	@Override
	public List<ClientFilter> getFilters() {
		return filters;
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#setFilters(java.util.List)
	 */
	@Override
	public void setFilters(List<ClientFilter> filters) {
		this.filters = filters;
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#setHttpClient(com.sun.jersey.api.client.Client)
	 */
	@Override
	public void setHttpClient(Client httpClient) {
		this.httpClient = httpClient;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#get(java.lang.String)
	 */
	@Override
	public ClientResponse get(String uri) {
		return get(uri, MediaType.WILDCARD_TYPE, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#get(java.lang.String, org.zaizi.restclient.Parameters)
	 */
	@Override
	public ClientResponse get(String uri, Parameters parameters) {
		return get(uri, MediaType.WILDCARD_TYPE, parameters);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#get(java.lang.String, javax.ws.rs.core.MediaType)
	 */
	@Override
	public ClientResponse get(String uri, MediaType acceptType) {
		return get(uri, acceptType, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#get(java.lang.String, javax.ws.rs.core.MediaType, org.zaizi.restclient.Parameters)
	 */
	@Override
	public ClientResponse get(String uri, MediaType acceptType, Parameters parameters) {
		
		URL url;
		try {
			url = buildURL(uri, parameters);
		} catch(MalformedURLException e) {
			throw new RestClientException("Error building the URL " + e.getMessage(), e);
		}
		
		httpClient.setFollowRedirects(true);
		
		WebResource resource = httpClient.resource(url.toString());
		applyFilters(resource);
		
		ClientResponse response = resource.accept(acceptType)
		                                  .get(ClientResponse.class);
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#post(java.lang.String, java.lang.Object, javax.ws.rs.core.MediaType, javax.ws.rs.core.MediaType)
	 */
	@Override
	public ClientResponse post(String uri, Object object, MediaType objectType, MediaType acceptType) {
		return post(uri, object, objectType, acceptType, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#post(java.lang.String, java.lang.Object, javax.ws.rs.core.MediaType, javax.ws.rs.core.MediaType, org.zaizi.restclient.Parameters)
	 */
	@Override
	public ClientResponse post(String uri, Object object, MediaType objectType, MediaType acceptType, Parameters parameters) {
		URL url;
		try {
			url = buildURL(uri, parameters);
		} catch(MalformedURLException e) {
			throw new RestClientException("Error building the URL " + e.getMessage(), e);
		}
		
		httpClient.setFollowRedirects(false);
		
		WebResource resource = httpClient.resource(url.toString());
		applyFilters(resource);
		
		ClientResponse response = resource.accept(acceptType)
                                          .entity(object, objectType)
		                                  .post(ClientResponse.class);
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#post(java.lang.String, java.io.InputStream, javax.ws.rs.core.MediaType, javax.ws.rs.core.MediaType)
	 */
	@Override
	public ClientResponse post(String uri, InputStream is, MediaType mimetype, MediaType type) {
		return post(uri, is, mimetype, type, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#post(java.lang.String, java.io.InputStream, javax.ws.rs.core.MediaType, javax.ws.rs.core.MediaType, org.zaizi.restclient.Parameters)
	 */
	@Override
	public ClientResponse post(String uri, InputStream is, MediaType mimetype, MediaType type, Parameters parameters) {
		URL url;
		try {
			url = buildURL(uri);
		} catch(MalformedURLException e) {
			throw new RestClientException("Error building the URL " + e.getMessage(), e);
		}
		
		httpClient.setFollowRedirects(false);
		
		WebResource resource = httpClient.resource(url.toString());
		applyFilters(resource);
		
		ClientResponse response = resource.accept(MediaType.WILDCARD_TYPE)
                                          .entity(is, mimetype)
		                                  .post(ClientResponse.class);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#put(java.lang.String, java.lang.Object, javax.ws.rs.core.MediaType, javax.ws.rs.core.MediaType)
	 */
	@Override
	public ClientResponse put(String uri, Object object, MediaType objectType, MediaType acceptType) {
		URL url;
		try {
			url = buildURL(uri);
		} catch(MalformedURLException e) {
			throw new RestClientException("Error building the URL " + e.getMessage(), e);
		}
		
		httpClient.setFollowRedirects(false);
		
		WebResource resource = httpClient.resource(url.toString());
		applyFilters(resource);
		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE)
                                          .entity(object, acceptType)
		                                  .put(ClientResponse.class);
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#put(java.lang.String, java.lang.Object, javax.ws.rs.core.MediaType, javax.ws.rs.core.MediaType, org.zaizi.restclient.Parameters)
	 */
	@Override
	public ClientResponse put(String uri, Object object, MediaType objectType, MediaType acceptType, Parameters parameters)
	{
		URL url;
		try {
			url = buildURL(uri, parameters);
		} catch(MalformedURLException e) {
			throw new RestClientException("Error building the URL " + e.getMessage(), e);
		}
		
		httpClient.setFollowRedirects(false);
		
		WebResource resource = httpClient.resource(url.toString());
		applyFilters(resource);
		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE)
                                          .entity(object, acceptType)
		                                  .put(ClientResponse.class);
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#put(java.lang.String, java.io.InputStream, javax.ws.rs.core.MediaType, javax.ws.rs.core.MediaType)
	 */
	@Override
	public ClientResponse put(String uri, InputStream is, MediaType mimetype, MediaType acceptType) {
		return put(uri, is, mimetype, acceptType, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#put(java.lang.String, java.io.InputStream, javax.ws.rs.core.MediaType, javax.ws.rs.core.MediaType, org.zaizi.restclient.Parameters)
	 */
	@Override
	public ClientResponse put(String uri, InputStream is, MediaType mimetype, MediaType acceptType, Parameters parameters) {
		URL url;
		try {
			url = buildURL(uri);
		} catch(MalformedURLException e) {
			throw new RestClientException("Error building the URL " + e.getMessage(), e);
		}
		
		httpClient.setFollowRedirects(false);
		
		WebResource resource = httpClient.resource(url.toString());
		applyFilters(resource);
		
		ClientResponse response = resource.accept(MediaType.WILDCARD_TYPE)
                                          .entity(is, mimetype)
		                                  .put(ClientResponse.class);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#delete(java.lang.String)
	 */
	@Override
	public ClientResponse delete(String uri) {
		return delete(uri, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.restclient.RestClient#delete(java.lang.String, org.zaizi.restclient.Parameters)
	 */
	@Override
	public ClientResponse delete(String uri, Parameters parameters) {
		URL url;
		try {
			url = buildURL(uri);
		} catch(MalformedURLException e) {
			throw new RestClientException("Error building the URL " + e.getMessage(), e);
		}
		
		httpClient.setFollowRedirects(true);
		
		WebResource resource = httpClient.resource(url.toString());
		applyFilters(resource);
		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE)
		                                  .delete(ClientResponse.class);
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
		sb.append(endpoint.toString());
		sb.append(uri);
		
		if(parameters != null) {
			if(!uri.contains("?")) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			
			Boolean first = Boolean.TRUE;
			for(Map.Entry<String, String> parameter : parameters.entrySet()) {
				try {
					String encodedParameter = URLEncoder.encode(parameter.getValue(), "UTF-8");
					sb.append(!first ? "&" : "")
					  .append(parameter.getKey())
					  .append("=")
					  .append(encodedParameter);		
				} catch(UnsupportedEncodingException e) {
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
	private void applyFilters(WebResource resource) {
		if(filters != null) {
			for(ClientFilter filter : filters) {
				resource.addFilter(filter);
			}
		}
	}
}
