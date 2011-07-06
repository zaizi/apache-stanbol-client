package org.zaizi.stanbol.services.exception;

/**
 * Generic exception for Stanbol services
 * 
 * @author efoncubierta
 *
 */
public class StanbolServiceException extends Exception {
	
	private static final long serialVersionUID = 8217651601375216765L;

	/**
	 * Constructor
	 * 
	 * @param msg Exception message
	 */
	public StanbolServiceException(String msg) {
		super(msg);
	}
	
	/**
	 * Constructor
	 * 
	 * @param msg Exception message
	 * @param e Catched exception
	 */
	public StanbolServiceException(String msg, Throwable e) {
		super(msg, e);
	}
}
