package org.zaizi.restclient;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implement a service endpoint
 * 
 * @author efoncubierta
 * 
 */
public class Endpoint {
	
	// Default values
	public static final String DEFAULT_PROTOCOL = "http";
	public static final String DEFAULT_HOST = "localhost";
	public static final String DEFAULT_PORT = "80";
	
	// Constants
	public static final String PROTOCOL_HTTP  = "http";
	public static final String PROTOCOL_HTTPS = "https";
	
	// Properties
	private String protocol;
	private String host;
	private String port;
	private String uri;
	
	/**
	 * Set a URL
	 * @param url URL
	 * @throws MalformedURLException
	 */
	public void setUrl(String url) throws MalformedURLException {
		URL u = new URL(url);
		this.protocol = u.getProtocol();
		this.host = u.getHost();
		this.port = "" + u.getPort();
		this.uri = u.getPath();
	}
	
	/**
	 * Get the endpoint protocol (http, https...)
	 * 
	 * @return Protocol
	 */
	public String getProtocol() {
		return protocol;
	}
	
	/**
	 * Set the endpoint protocol (http, https...)
	 * 
	 * @param protocol Protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	/**
	 * Get the endpoint host
	 * 
	 * @return Host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Set the endpoint host
	 * 
	 * @param host Host
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Get the endpoint port
	 * 
	 * @return Port
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Set the endpoint port
	 * 
	 * @param port Port
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	/**
	 * Get the enpoint base URI
	 * 
	 * @return Base URI
	 */
	public String getUri() {
		return uri;
	}
	
	/**
	 * Set the endpoint base URI
	 * 
	 * @param uri Base URI
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * Represent the endpoint as a String
	 * 
	 * @return Endpoint as a String
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol == null ? DEFAULT_PROTOCOL : protocol);
		sb.append("://");
		sb.append(host == null ? DEFAULT_HOST : host);
		sb.append(":");
		sb.append(port == null ? DEFAULT_PORT : port);
		sb.append(uri == null ? "" : uri);
		return sb.toString();
	}
}
