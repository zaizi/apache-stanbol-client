package org.zaizi.stanbol.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.zaizi.restclient.RestClient;
import org.zaizi.stanbol.client.model.Enhancement;
import org.zaizi.stanbol.client.model.EnhancementParser;
import org.zaizi.stanbol.services.StanbolEnhancerService;
import org.zaizi.stanbol.services.exception.StanbolServiceException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Implementation of {@link StanbolEnhancerService}
 * 
 * @author efoncubierta
 *
 */
public class StanbolEnhancerServiceImpl
	extends StanbolServiceAbstract implements StanbolEnhancerService {
	
	private Logger logger = Logger.getLogger(StanbolEnhancerServiceImpl.class);
	
	/**
	 * Constructor
	 * 
	 * @param restClient REST Client
	 */
	public StanbolEnhancerServiceImpl(RestClient restClient) {
		super(restClient);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.services.StanbolEnhancerService#enhance(java.io.File)
	 */
	@Override
	public List<Enhancement> enhance(File file) throws StanbolServiceException {
		try {
			return enhance(new FileInputStream(file));
		} catch(FileNotFoundException e) {
			throw new StanbolServiceException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.services.StanbolEnhancerService#enhance(java.io.InputStream)
	 */
	@Override
	public List<Enhancement> enhance(InputStream is) throws StanbolServiceException {
		
        ClientResponse response = getRestClient().put(STANBOL_ENGINES_PATH, is,
        		MediaType.TEXT_PLAIN_TYPE, MediaType.WILDCARD_TYPE);
        
        int status = response.getStatus();
        if(status != 200 && status != 201 && status != 202) {
        	throw new StanbolServiceException(
        			"[HTTP " + status + "] Error while enhancing content into stanbol server"
        		);
        }
        
        if(logger.isDebugEnabled())
    	{
        	logger.debug("Content has been sucessfully enhanced");
    	}
        
        // Parse the RDF model
        Model model = ModelFactory.createDefaultModel();
    	model.read(response.getEntityInputStream(), null);
    	
		return EnhancementParser.parse(model);
	}
}