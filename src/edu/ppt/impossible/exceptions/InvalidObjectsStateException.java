package edu.ppt.impossible.exceptions;

public class InvalidObjectsStateException extends RuntimeException {

	private static final long serialVersionUID = 1027331587326064410L;

	public InvalidObjectsStateException(String message) {
		super(message);
	}

}
