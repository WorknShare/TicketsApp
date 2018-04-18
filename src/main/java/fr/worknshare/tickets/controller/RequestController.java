package fr.worknshare.tickets.controller;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

/**
 * Interface used on controllers needing an HttpClient and HttpContext to make requests
 * @author Jérémy LAMBERT
 * 
 * @see HttpClient
 * @see HttpContext
 *
 */
interface RequestController {

	/**
	 * Set the Http client used for requests
	 * @param client
	 */
	public void setHttpClient(HttpClient client);
	
	/**
	 * Set the Http context used for requests
	 * @param context
	 */
	public void setHttpContext(HttpContext context);
	
}
