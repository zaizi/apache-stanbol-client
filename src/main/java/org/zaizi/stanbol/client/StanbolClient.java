package org.zaizi.stanbol.client;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.zaizi.stanbol.client.exception.StanbolClientException;
import org.zaizi.stanbol.client.model.ContentItem;
import org.zaizi.stanbol.client.model.Enhancement;

/**
 * Define operations for Stanbol Services
 * 
 * @author efoncubierta
 *
 */
public interface StanbolClient
{
	/**
	 * Create a content into contenthub/
	 * 
	 * @param id Content id
	 * @param file Content file
	 * @throws StanbolClientException
	 */
	public void addContent(String id, File file)
		throws StanbolClientException;
	
	/**
	 * Create a content into contenthub/
	 * 
	 * @param id Content id
	 * @param is Content input stream
	 * @throws StanbolClientException
	 */
	public void addContent(String id, InputStream is)
		throws StanbolClientException;
	
	/**
	 * Extract the content metadata from contenthub/
	 * 
	 * @param id Content id
	 * @return Content metadata
	 * @throws StanbolClientException
	 */
	public ContentItem getMetadata(String id)
		throws StanbolClientException;
	
	/**
	 * Extract content enhancements from engines/
	 * 
	 * @param file Content file
	 * @return List of enhancements
	 * @throws StanbolClientException
	 */
	public List<Enhancement> enhance(File file)
		throws StanbolClientException;
	
	/**
	 * Extract content enhancements from engines/
	 * 
	 * @param is Content input stream
	 * @return List of enhancements
	 * @throws StanbolClientException
	 */
	public List<Enhancement> enhance(InputStream is)
		throws StanbolClientException;
}
