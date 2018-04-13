package fr.worknshare.tickets.repository;

/**
 * Custom Runnable used as callbacks for REST requests when a single object is expected in response
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 */
public abstract class ObjectCallback<T> implements Runnable {

	private T object;

	protected void setObject(T object) {
		this.object = object;
	}

	/**
	 * Get the response from the request. All checks have to be done.
	 * @return the response, can be null
	 */
	public T getObject() {
		return object;
	}

}
