package fr.worknshare.tickets.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.Config;
import fr.worknshare.tickets.model.Model;
import fr.worknshare.tickets.networking.HttpMethod;
import fr.worknshare.tickets.networking.RequestCallback;
import fr.worknshare.tickets.networking.RestRequest;
import fr.worknshare.tickets.networking.RestResponse;
import fr.worknshare.tickets.view.Paginator;

/**
 * Data controller for models. Requests, retrieve and send data to the REST API.
 * 
 * @author Jérémy LAMBERT
 *
 */
public abstract class Repository<T extends Model<T>> {

	private HttpClient httpClient;
	private HttpContext httpContext;
	private SimpleDateFormat dateFormat;

	private Hashtable<Integer, T> models; //Hashtable is synchronized thus thread safe

	public Repository(HttpClient client, HttpContext context) {
		this();
		this.httpClient  = client;
		this.httpContext = context;
	}

	public Repository() {
		dateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.models = new Hashtable<>();
	}

	/**
	 * Get the name of the resource. Will be used in URLs when doing requests.
	 * <br>
	 * For example, a resource having the name "user" will request the REST API to "http://host/api/user" automatically.
	 * @return the name of the resource
	 */
	public abstract String getResourceName();

	/**
	 * Get the list of records for the given page.
	 * @param page - the page number, must be positive
	 * @param callback - the callback to execute when the request is done
	 * @param failCallback - the callback to execute when the request failed
	 */
	public final void paginate(int page, PaginatedRequestCallback<T> callback, FailCallback failCallback) {
		paginate(page, null, callback, failCallback);
	}

	/**
	 * Get the list of records for the given page.
	 * @param page - the page number, must be positive
	 * @param filter - nullable object to use as filter
	 * @param callback - the callback to execute when the request is done
	 * @param failCallback - the callback to execute when the request failed
	 */
	public final void paginate(int page, Object filter, PaginatedRequestCallback<T> callback, FailCallback failCallback) {	
		request("page", page, filter, new JsonCallback() {

			@Override
			public void run() {
				PaginatedResponse<T> response = null;
				ArrayList<T> list;
				JsonObject data = getObject();
				JsonElement elem = data.get("paginator");
				if(elem != null && elem.isJsonObject()) {
					Paginator paginator = Paginator.fromJson(elem.getAsJsonObject());
					elem = data.get("items");
					if(elem != null && elem.isJsonArray()) {
						list = parseArray(elem.getAsJsonArray());
						response = new PaginatedResponse<T>(paginator, list);
						callback.setResponse(getResponse());
						callback.setPaginatedResponse(response);
						callback.run();
					} else
						handleMalformedResponse(getResponse(), failCallback, "items");

				} else 
					handleMalformedResponse(getResponse(), failCallback, "paginator");
			}

		}, failCallback);
	}

	/**
	 * Request the list of records corresponding to the "search" pattern.
	 * @param search - the search criteria
	 * @param callback - the callback to execute when the request is done
	 * @param failCallback - the callback to execute if the request fails
	 */
	public final void where(String search, PaginatedRequestCallback<T> callback, FailCallback failCallback) {
		where(search, null, callback, failCallback);
	}

	/**
	 * Request the list of records corresponding to the "search" pattern.
	 * @param search - the search criteria
	 * @param filter - nullable object to use as filter
	 * @param callback - the callback to execute when the request is done
	 * @param failCallback - the callback to execute if the request fails
	 */
	public final void where(String search, Object filter, PaginatedRequestCallback<T> callback, FailCallback failCallback) {
		request("search", search, filter, new JsonCallback() {

			@Override
			public void run() {
				PaginatedResponse<T> response = null;
				ArrayList<T> list = null;
				JsonObject data = getObject();
				JsonElement elem = data.get("items");
				if(elem != null && elem.isJsonArray()) {
					list = parseArray(elem.getAsJsonArray());
					response = new PaginatedResponse<T>(new Paginator(1, 1, 10), list);
					callback.setResponse(getResponse());
					callback.setPaginatedResponse(response);
					callback.run();
				} else
					handleMalformedResponse(getResponse(), failCallback, "items");

			}

		}, failCallback);
	}

	/**
	 * Executes a simple array request using the GET method with a single parameter and returns the raw result (data member).
	 * @param paramName - the name of the parameter
	 * @param paramValue - the value of the parameter
	 * @param filter - nullable object to use as filter 
	 * @param callback - the callback to execute when the request is done
	 * @param failCallback - the callback to execute if the request fails
	 */
	protected final void request(String paramName, Object paramValue, Object filter, JsonCallback callback, FailCallback failCallback) {
		request(paramName, paramValue, filter, getUrl(), callback, failCallback);
	}

	/**
	 * Executes a simple array request using the GET method with a single parameter and returns the raw result (data member).
	 * @param paramName - the name of the parameter
	 * @param paramValue - the value of the parameter
	 * @param callback - the callback to execute when the request is done
	 * @param failCallback - the callback to execute if the request fails
	 */
	protected final void request(String paramName, Object paramValue, JsonCallback callback, FailCallback failCallback) {
		request(paramName, paramValue, null, getUrl(), callback, failCallback);
	}

	/**
	 * Executes a simple array request using the GET method with a single parameter and returns the raw result (data member).
	 * @param paramName - the name of the parameter
	 * @param paramValue - the value of the parameter
	 * @param url - the request url
	 * @param filter - nullable object to use as filter
	 * @param callback - the callback to execute when the request is done
	 * @param failCallback - the callback to execute if the request fails
	 */
	protected final void request(String paramName, Object paramValue, Object filter, String url, JsonCallback callback, FailCallback failCallback) {
		RestRequest request = new RestRequest(httpClient, url)
				.setUrlParam(true)
				.context(httpContext);

		if(paramName != null & paramValue != null)
			request.param(paramName, paramValue);

		if(filter != null)
			request.param("filter", filter);

		request.asyncExecute(HttpMethod.GET, new RequestCallback() {

			@Override
			public void run() {
				RestResponse response = getResponse();
				if(response != null && response.getStatus() == 200) {
					JsonObject payload = response.getJsonObject();
					JsonElement data = payload.get("data");
					if(data != null && data.isJsonObject()) {
						callback.setResponse(response);
						callback.setObject(data.getAsJsonObject());
						callback.run();
					} else
						handleMalformedResponse(getResponse(), failCallback, "data");

				} else {
					failCallback.setResponse(response);
					failCallback.run();
					Logger.getGlobal().warning("Repository request failed: " + response.getStatus() + " " + failCallback.getMessage());
				}
			}

		});
	}

	/**
	 * Get a single record for this model based on its ID.
	 * @param id
	 * @param callback - the callback to execute when the request is done
	 * @param failCallback - the callback to execute if the request fails
	 * @return an instance of the model, null if not found
	 * 
	 * @throws IllegalArgumentException if id is not positive.
	 */
	public void getById(int id, ObjectCallback<T> callback, FailCallback failCallback) {
		if(id < 1) throw new IllegalArgumentException("Requested resource's ID must be positive. " + id + " given.");

		RestRequest request = new RestRequest(httpClient, getUrl(id)).context(httpContext);

		request.asyncExecute(HttpMethod.GET, new RequestCallback() {

			@Override
			public void run() {
				RestResponse response = getResponse();
				if(response != null && response.getStatus() == 200) {
					JsonObject payload = response.getJsonObject();
					JsonElement data = payload.get("data");
					if(data != null && data.isJsonObject()) {
						callback.setResponse(response);
						callback.setObject(parseObject(data.getAsJsonObject()));
						callback.run();
					} else
						handleMalformedResponse(getResponse(), failCallback, "data");
					
				} else {
					failCallback.setResponse(response);
					failCallback.run();
					Logger.getGlobal().warning("Repository getById request failed: " + response.getStatus() + " " + failCallback.getMessage());
				}
			}

		});
	}

	protected void handleMalformedResponse(RestResponse response, FailCallback failCallback, String expected) {
		failCallback.setResponse(response);
		failCallback.setMessage("Réponse malformée");
		failCallback.run();
		Logger.getGlobal().warning("Malformed paginate response (missing expected \"" + expected + "\"):\n\t" + response.getRaw());
	}

	/**
	 * Generate the URL based on the Host in the config and the resource name
	 * @return the url to make a request for this model
	 */
	protected final String getUrl() {
		String host = Config.getInstance().get("Host");
		if(host == null) throw new NullPointerException("Host is undefined");

		return host + "/api/" + getResourceName();
	}

	/**
	 * Generate the URL based on the Host in the config, the resource name and the ID of the resource
	 * @param id - the id of the resource, must be positive
	 * @return the full url to make a request for this model and resource
	 */
	protected final String getUrl(int id) {
		return getUrl() + "/" + id;
	}

	/**
	 * Iterate through the response array and create a list of models
	 * @param array - the array from the response containing the objects
	 * @return a list of models with filled values
	 */
	public ArrayList<T> parseArray(JsonArray array) {
		ArrayList<T> list = new ArrayList<T>();
		array.forEach((element) -> {
			if(element.isJsonObject())
				list.add(parseObject(element.getAsJsonObject()));
		});
		return list;
	}

	/**
	 * Create an instance of the model based on the given JsonElement.
	 * @param element - the element from the response
	 * @return an instance of the model with filled values
	 */
	public abstract T parseObject(JsonObject object);

	/**
	 * Get the HttpClient used for requests in this repository
	 * @return the HttpClient used for requests in this repository
	 * 
	 * @see HttpClient
	 */
	public final HttpClient getHttpClient() {
		return httpClient;
	}

	/**
	 * Set the HttpClient used for requests in this repository
	 * 
	 * @see HttpClient
	 */
	public final void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * Get the HttpContext used for requests in this repository
	 * @return the HttpContext used for requests in this repository
	 * 
	 * @see HttpContext
	 */
	public final HttpContext getHttpContext() {
		return httpContext;
	}

	/**
	 * Set the HttpContext used for requests in this repository
	 * 
	 * @see HttpContext
	 */
	public final void setHttpContext(HttpContext httpContext) {
		this.httpContext = httpContext;
	}

	public final SimpleDateFormat getDateFormatter() {
		return dateFormat;
	}

	/**
	 * Store a loaded model into the cache.
	 * @param model
	 */
	protected void registerModel(T model) {
		models.put(new Integer(model.getId().get()), model);
	}

	/**
	 * Get a model from the cache.
	 * @param id - the id
	 * @return the model stored in cache,or null if not found
	 */
	public T getFromCache(Integer id) {
		return models.get(id);
	}

	/**
	 * Clear all models stored in cache. This does not guarantee they are freed from memory.
	 */
	public void clearCache() {
		models.clear();
	}

}
