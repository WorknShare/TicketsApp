package fr.worknshare.tickets.repository;

/**
 * Custom Runnable used as callbacks for REST paginated requests
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 * @see PaginatedResponse
 */
public abstract class PaginatedRequestCallback<T> extends RestCallback {

	private PaginatedResponse<T> response;
	
	protected void setPaginatedResponse(PaginatedResponse<T> response) {
		this.response = response;
	}
	
	/**
	 * Get the response from the request. All checks have to be done.
	 * @return the response, can be null
	 */
	public PaginatedResponse<T> getPaginatedResponse() {
		return response;
	}

}	
