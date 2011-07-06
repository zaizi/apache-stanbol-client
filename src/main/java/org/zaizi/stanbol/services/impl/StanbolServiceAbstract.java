package org.zaizi.stanbol.services.impl;

import org.zaizi.restclient.RestClient;
import org.zaizi.stanbol.services.StanbolService;

/**
 * Abstract utility class for Stanbol Services
 * 
 * @author efoncubierta
 *
 */
public abstract class StanbolServiceAbstract implements StanbolService {

	private RestClient restClient;
	
	/**
	 * Constructor
	 * 
	 * @param restClient REST Client
	 */
	public StanbolServiceAbstract(RestClient restClient) {
		this.restClient = restClient;
	}
    
    /**
     * Get a REST Client for Stanbol
     * 
     * @return REST Client
     */
    protected RestClient getRestClient() {
    	return this.restClient;
    }
}
