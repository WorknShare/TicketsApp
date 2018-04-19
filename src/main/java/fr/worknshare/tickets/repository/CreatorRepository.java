package fr.worknshare.tickets.repository;

import fr.worknshare.tickets.networking.RequestCallback;

/**
 * Interface for repositories which can create resources.
 * @author Jérémy LAMBERT
 *
 * @param <T>
 * 
 * @see Repository
 */
public interface CreatorRepository<T> {

	/**
	 * Create a new resource on the server
	 * @param resource - the object to send on the server to be saved
	 * @param callback - the callback to execute when the request is done
	 */
	void create(T resource, RequestCallback callback);
	
}
