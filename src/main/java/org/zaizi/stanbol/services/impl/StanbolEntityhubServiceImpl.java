package org.zaizi.stanbol.services.impl;

import org.zaizi.restclient.RestClient;
import org.zaizi.stanbol.services.StanbolEntityhubService;

public class StanbolEntityhubServiceImpl
	extends StanbolServiceAbstract implements StanbolEntityhubService {

	/**
	 * Constructor
	 * 
	 * @param restClient REST Client
	 */
	public StanbolEntityhubServiceImpl(RestClient restClient) {
		super(restClient);
	}

}
