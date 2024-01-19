package org.jenjetsu.com.finalproject.exception;

public class EntityNotFoundException extends RuntimeException{

	public EntityNotFoundException() {
		super();
	}
	
	public EntityNotFoundException(String message) {
		super(message);
	}
	
	public EntityNotFoundException(Exception cause) {
		super(cause);
	}
	
	public EntityNotFoundException(String message, Exception cause) {
		super(message, cause);
	}
}
