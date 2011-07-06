package org.zaizi.stanbol.client.exception;

/**
 * Generic exception for stanbol client
 * 
 * @author efoncubierta
 *
 */
public class StanbolClientException extends Exception {
	private static final long serialVersionUID = 784007711275322771L;

	/**
	 * Constructor
	 * 
	 * @param msg Exception message
	 */
	public StanbolClientException(String msg) {
		super(msg);
	}
	
	/**
	 * Constructor
	 * 
	 * @param msg Exception message
	 * @param e Catched exception
	 */
	public StanbolClientException(String msg, Throwable e) {
		super(msg, e);
	}
}
