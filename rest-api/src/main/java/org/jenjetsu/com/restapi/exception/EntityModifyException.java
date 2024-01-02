package org.jenjetsu.com.restapi.exception;

public class EntityModifyException extends RuntimeException{

	public EntityModifyException() {
		super();
	}
	
	public EntityModifyException(String message) {
		super(message);
	}
	
	public EntityModifyException(Throwable cause) {
		super(cause);
	}
	
	public EntityModifyException(String message, Throwable cause) {
		super(message, cause);
	}
}
