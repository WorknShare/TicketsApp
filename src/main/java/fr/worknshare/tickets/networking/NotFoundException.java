package fr.worknshare.tickets.networking;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 4800168628239251143L;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
