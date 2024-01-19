package org.jenjetsu.com.finalproject.exception;

public class EntityDeleteException extends RuntimeException{

	public EntityDeleteException() {
		super();
	}
	
	public EntityDeleteException(String message) {
		super(message);
	}
	
	public EntityDeleteException(Throwable cause) {
		super(cause);
	}
	
	public EntityDeleteException(String message, Throwable cause) {
		super(message, cause);
	}
}
