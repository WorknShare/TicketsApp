package fr.worknshare.tickets.networking;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;

/**
 * Rest chainable request builder.
 * 
 * @author Jérémy LAMBERT
 * 
 * @see RestResult
 *
 */
public class RestBuilder {

	private String url;
	private Hashtable<String, Object> parameters;
	
	public RestBuilder(String url) {
		this.url = url;
		this.parameters = new Hashtable<>();
	}
	
	public RestBuilder() {}
	
	/**
	 * Set the request URL
	 * @param url
	 * @return current instance, used to chain the builder
	 */
	public RestBuilder url(String url) {
		this.url = url;
		return this;
	}
	
	/**
	 * Get the request URL
	 * @return the request URL
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Executes the request using the GET http verb
	 * @return the result of the request
	 * @see RestResult
	 */
	public RestResult get() {
		//TODO
		return null;
	}
	
	/**
	 * Executes the request using the POST http verb
	 * @return the result of the request
	 * @see RestResult
	 */
	public RestResult post() {
		
		RestResult result;
		
		try {
			StringEntity param = serializeParameters();
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
	
			// Request headers
			request.addHeader("Accept", "application/json");
			request.addHeader("Content-type", "application/json;charset=UTF-8");
			
			request.setEntity(param);
			
			HttpResponse response = client.execute(request);
			result = new RestResult(response);
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Unable to execute Rest request", e);
			result = new RestResult();
		}

		return result;
		
	}
	
	
	/**
	 * Puts a parameter to the request. Overrides if a value with the given name already exists
	 * @param the name of the parameter
	 * @param the value associated with the given name, should implement Serializable
	 * @return current instance, used to chain the builder
	 * @see Serializable
	 */
	public RestBuilder param(String name, Object value) {
		parameters.put(name, value);
		return this;
	}
	
	/**
	 * Serialize the parameters in json
	 * @return the json string representing parameters
	 * @throws UnsupportedEncodingException 
	 * @see StringEntity
	 */
	private StringEntity serializeParameters() throws UnsupportedEncodingException {
		Gson gson = new Gson();
		StringEntity jsonparam = new StringEntity(gson.toJson(parameters));
		jsonparam.setChunked(true);
		return jsonparam;
	}
	
}
