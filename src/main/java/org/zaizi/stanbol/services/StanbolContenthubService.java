package org.zaizi.stanbol.services;

import java.io.File;
import java.io.InputStream;

import org.zaizi.stanbol.client.model.ContentItem;
import org.zaizi.stanbol.services.exception.StanbolServiceException;

/**
 * Define operations for Stanbol Enhancer
 * 
 * @author efoncubierta
 *
 */
public interface StanbolContenthubService extends StanbolService {
	
	// Stanbol Contenthub services URLs
	public static final String STANBOL_STORE_PATH = "contenthub/";
	public static final String STANBOL_STORE_CONTENT_PATH = STANBOL_STORE_PATH + "content/";
	public static final String STANBOL_STORE_METADATA_PATH = STANBOL_STORE_PATH + "metadata/";
	
	/**
	 * Create a content
	 * 
	 * @param id Content ID
	 * @param file File
	 * @return Content item
	 * @throws StanbolServiceException
	 */
	public ContentItem add(String id, File file) throws StanbolServiceException;
	
	/**
	 * Create a content
	 * 
	 * @param id Content ID
	 * @param is InputStream
	 * @return Content item
	 * @throws StanbolServiceException
	 */
	public ContentItem add(String id, InputStream is) throws StanbolServiceException;

	/**
	 * Get metadata information of a content
	 * 
	 * @param id Content ID
	 * @return Content item
	 * @throws StanbolServiceException
	 */
	public ContentItem get(String id) throws StanbolServiceException;
}
