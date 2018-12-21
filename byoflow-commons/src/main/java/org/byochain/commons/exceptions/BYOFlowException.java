package org.byochain.commons.exceptions;

/**
 * BYOFlowException
 * @author Giuseppe Vincenzi
 *
 */
public class BYOFlowException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3598519926879443947L;
	
	/**
	 * BYOFlowException Constructor
	 * 
	 * @param e Exception
	 */
	public BYOFlowException(Exception e) {
		super(e.getMessage());
	}
	
	/**
	 * BYOFlowException Constructor
	 * 
	 * @param message String
	 */
	public BYOFlowException(String message) {
		super(message);
	}

	/**
	 * BYOFlowException Constructor
	 * 
	 * @param t Throwable
	 */
	public BYOFlowException(Throwable t) {
		super(t);
	}
}
