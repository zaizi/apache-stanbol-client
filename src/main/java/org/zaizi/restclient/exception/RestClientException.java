package org.zaizi.restclient.exception;

/**
 * Exception thrown by the rest client
 * 
 * @author efoncubierta
 *
 */
public class RestClientException extends RuntimeException
{

	private static final long serialVersionUID = 4485522067782124440L;

	/**
	 * Constructor
	 * 
	 * @param msg Error message
	 */
	public RestClientException(String msg)
	{
		super(msg);
	}
	
	/**
	 * Constructor
	 * 
	 * @param msg Error message
	 * @param cause Exception
	 */
	public RestClientException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
