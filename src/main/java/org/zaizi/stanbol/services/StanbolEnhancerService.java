package org.zaizi.stanbol.services;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.zaizi.stanbol.client.model.Enhancement;
import org.zaizi.stanbol.services.exception.StanbolServiceException;

/**
 * Define operations for Stanbol Enhancer
 * 
 * @author efoncubierta
 *
 */
public interface StanbolEnhancerService extends StanbolService {
	
	// Stanbol Enhancer service url
	public static final String STANBOL_ENGINES_PATH = "engines/";
	
	/**
	 * Enhance a content
	 * 
	 * @param file File
	 * @return List of enhancements
	 * @throws StanbolServiceException
	 */
	public List<Enhancement> enhance(File file) throws StanbolServiceException;
	
	/**
	 * Enhance a content
	 * 
	 * @param is InputStream
	 * @return List of enhancements
	 * @throws StanbolServiceException
	 */
	public List<Enhancement> enhance(InputStream is) throws StanbolServiceException;
}
