package org.zaizi.stanbol.client.impl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import org.zaizi.restclient.Endpoint;
import org.zaizi.restclient.RestClient;
import org.zaizi.restclient.RestClientImpl;
import org.zaizi.stanbol.client.StanbolClient;
import org.zaizi.stanbol.client.exception.StanbolClientException;
import org.zaizi.stanbol.client.model.ContentItem;
import org.zaizi.stanbol.client.model.Enhancement;
import org.zaizi.stanbol.services.StanbolContenthubService;
import org.zaizi.stanbol.services.StanbolEnhancerService;
import org.zaizi.stanbol.services.StanbolEntityhubService;
import org.zaizi.stanbol.services.exception.StanbolServiceException;
import org.zaizi.stanbol.services.impl.StanbolContenthubServiceImpl;
import org.zaizi.stanbol.services.impl.StanbolEnhancerServiceImpl;
import org.zaizi.stanbol.services.impl.StanbolEntityhubServiceImpl;

import com.sun.jersey.api.client.Client;

/**
 * Implementation of {@link StanbolClient}
 * 
 * @author efoncubierta
 *
 */
public class StanbolClientImpl implements StanbolClient
{	
	// Stanbol services
	private StanbolEnhancerService enhancer;
	private StanbolContenthubService contenthub;
	private StanbolEntityhubService entityhub;
	
	/**
	 * Constructor
	 * 
	 * @param url URL to Stanbol server
	 * @throws StanbolClientException
	 */
	public StanbolClientImpl(String url) throws StanbolClientException {
		final Endpoint endpoint = new Endpoint();
		
		try {
			endpoint.setUrl(url);
		} catch(MalformedURLException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
		
		final RestClient restClient = new RestClientImpl();
		restClient.setHttpClient(Client.create());
		restClient.setEndpoint(endpoint);
		
		enhancer   = new StanbolEnhancerServiceImpl(restClient);
		entityhub  = new StanbolEntityhubServiceImpl(restClient);
		contenthub = new StanbolContenthubServiceImpl(restClient);
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#addContent(java.lang.String, java.io.File)
	 */
	@Override
	public void addContent(String id, File file) throws StanbolClientException {
		try {
			contenthub.add(id, file);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#addContent(java.lang.String, java.io.InputStream)
	 */
	@Override
	public void addContent(String id, InputStream is) throws StanbolClientException {
		try {
			contenthub.add(id, is);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#getMetadata(java.lang.String)
	 */
	@Override
	public ContentItem getMetadata(String id) throws StanbolClientException {
		try {
			return contenthub.get(id);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#enhance(java.io.File)
	 */
	@Override
	public List<Enhancement> enhance(File file) throws StanbolClientException {
		try {
			return enhancer.enhance(file);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#enhance(java.io.InputStream)
	 */
	@Override
	public List<Enhancement> enhance(InputStream is) throws StanbolClientException {
		try {
			return enhancer.enhance(is);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}
}
