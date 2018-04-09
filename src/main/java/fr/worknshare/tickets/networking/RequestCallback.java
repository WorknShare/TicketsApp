package fr.worknshare.tickets.networking;

/**
 * Custom Runnable usde as callbacks for REST requests
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 */
public abstract class RequestCallback implements Runnable {

	private RestResponse response;
	
	protected void setResponse(RestResponse response) {
		this.response = response;
	}
	
	/**
	 * Get the response from the request. All checks have to be done.
	 * @return the response, can be null
	 */
	public RestResponse getResponse() {
		return response;
	}

}	