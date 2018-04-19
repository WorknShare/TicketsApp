package fr.worknshare.tickets.repository;

import com.google.gson.JsonElement;

import fr.worknshare.tickets.networking.RestResponse;

/**
 * Custom Runnable used as callbacks for failed REST requests
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 */
public abstract class FailCallback extends RestCallback {

	private String message;

	protected void setResponse(RestResponse response) {
		super.setResponse(response);
		loadMessage();
	}

	private void loadMessage() {
		JsonElement element = getResponse().getJsonObject().get("error");
		if(element != null && element.isJsonPrimitive()) 
			message = element.getAsString();
		else
			message = "Erreur sur le serveur distant";
	}

	/**
	 * Get the error message
	 * @return the message from the response
	 */
	public String getMessage() {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get a message displayable to the user
	 * @return a built message for the user
	 */
	public String getFullMessage() {
		return getStatus() != 200 ? getStatus() + " : " + getMessage() : "Erreur interne : " + getMessage();
	}

}	
