package org.jenjetsu.com.todo.exception;

public class EntityCreateException extends RuntimeException {

	public EntityCreateException() {
		super();
	}
	
	public EntityCreateException(String message) {
		super(message);
	}
	
	public EntityCreateException(Throwable cause) {
		super(cause);
	}
	
	public EntityCreateException(String message, Throwable cause) {
		super(message, cause);
	}
}
