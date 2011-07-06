package org.zaizi.stanbol.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.zaizi.stanbol.client.impl.StanbolClientImpl;
import org.zaizi.stanbol.client.model.ContentItem;

@RunWith(JUnit4.class)
public class StanbolClientTest extends TestCase
{
	private Logger logger = Logger.getLogger(StanbolClientTest.class);
	
	private static final String TEST_PROPERTIES_FILE = "test.properties";
	
	// Properties
	private static final String STANBOL_SERVER_URL = "stanbol.server.url";
	private static final String CONTENT_TEST_PREFIX = "content.test.prefix";
	private static final String CONTENT_TEST_TIMESTAMP = "content.test.timestamp";
	private static final String CONTENT_TEST_FILENAME = "content.test.filename";
	
	// Default values
	private static final String STANBOL_SERVER_URL_DEFAULT = "http://localhost:8080/";
	private static final String CONTENT_TEST_PREFIX_DEFAULT = "test-";
	private static final String CONTENT_TEST_TIMESTAMP_DEFAULT = "yyyyMMddHHmmss";
	private static final String CONTENT_TEST_FILENAME_DEFAULT = "demo.txt";
	
	// System properties
	private static final String STANBOL_CLIENT_TEST_CONTENTID = "stanbol.client.test.contentId";
	
	
	final StanbolClient stanbolClient;
	final String stanbolServerUrl;
	final String contentTestPrefix;
	final String contentTestTimestamp;
	final String contentTestFilename;
	
	public StanbolClientTest() throws Exception
	{
		final Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/" + TEST_PROPERTIES_FILE));
		
		logger.info("Loaded properties file " + TEST_PROPERTIES_FILE);
		
		stanbolServerUrl = properties.getProperty(STANBOL_SERVER_URL, STANBOL_SERVER_URL_DEFAULT);
		contentTestPrefix = properties.getProperty(CONTENT_TEST_PREFIX, CONTENT_TEST_PREFIX_DEFAULT);
		contentTestTimestamp = properties.getProperty(CONTENT_TEST_TIMESTAMP, CONTENT_TEST_TIMESTAMP_DEFAULT);
		contentTestFilename = properties.getProperty(CONTENT_TEST_FILENAME, CONTENT_TEST_FILENAME_DEFAULT);
		
		logger.info("TEST configuration: ");
		logger.info("    - " + STANBOL_SERVER_URL + " = " + stanbolServerUrl);
		logger.info("    - " + CONTENT_TEST_PREFIX + " = " + contentTestPrefix);
		logger.info("    - " + CONTENT_TEST_TIMESTAMP + " = " + contentTestTimestamp);
		logger.info("    - " + CONTENT_TEST_FILENAME + " = " + contentTestFilename);
		
		stanbolClient = new StanbolClientImpl(stanbolServerUrl);
	}
	
	@Test
	public void test1_CreateContent() throws Exception
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(contentTestTimestamp);
		final String contentId = contentTestPrefix + sdf.format(new Date());
		// Set system property for future uses
		System.setProperty(STANBOL_CLIENT_TEST_CONTENTID, contentId);
		
		logger.info("Uploading content with id " + contentId);
		
		stanbolClient.addContent(contentId, this.getClass().getResourceAsStream("/" + contentTestFilename));
	}
	
	@Test
	public void test2_LoadContent() throws Exception
	{
		final String contentId = System.getProperty(STANBOL_CLIENT_TEST_CONTENTID);
		
		logger.info("Loading content with id " + contentId); 
		
		final ContentItem contentMetadata = stanbolClient.getMetadata(contentId);
		contentMetadata.getEnhancements();
		System.out.println(contentMetadata.toString());
	}
}
