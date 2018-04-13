package fr.worknshare.tickets.repository;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.Config;
import fr.worknshare.tickets.model.Model;
import fr.worknshare.tickets.networking.HttpMethod;
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

	public Repository(HttpClient client, HttpContext context) {
		this.httpClient = client;
		this.httpContext = context;
	}
	
	public Repository() {}

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
	 * @return a paginated response
	 * 
	 * @see PaginatedResponse
	 */
	public final PaginatedResponse<T> paginate(int page) {
		PaginatedResponse<T> response = null;
		ArrayList<T> list;
		JsonObject data = getRequest("page", page);
		if(data == null) return null;
		
		JsonElement elem = data.get("paginator");
		if(elem != null && elem.isJsonObject()) {
			Paginator paginator = Paginator.fromJson(elem.getAsJsonObject());
			elem = data.get("items");
			if(elem != null && elem.isJsonArray()) {
				list = parseArray(elem.getAsJsonArray());
				response = new PaginatedResponse<T>(paginator, list);
			} else {
				//TODO Malformed response 
			}
		} else {
			//TODO Malformed response
		}
		
		return response;
	}

	/**
	 * Request the list of records corresponding to the "search" pattern.
	 * @return a list of records
	 */
	public final ArrayList<T> where(String search) {
		ArrayList<T> list = null;
		JsonObject data = getRequest("search", search);
		JsonElement elem = data.get("items");
		if(elem != null && elem.isJsonArray()) {
				list = parseArray(elem.getAsJsonArray());
		} else {
				//TODO Malformed response 
		}
		return list;
	}

	/**
	 * Executes a simple array request using the GET method with a single parameter and returns the raw result (data member).
	 * @param paramName - the name of the parameter
	 * @param paramValue - the value of the parameter
	 * @return the json object "data" from the response
	 */
	private final JsonObject getRequest(String paramName, Object paramValue) {
		RestRequest request = new RestRequest(httpClient, getUrl())
				.setUrlParam(true)
				.context(httpContext)
				.param(paramName, paramValue);

		RestResponse response = request.execute(HttpMethod.GET);
		Logger.getGlobal().info(response.getRaw()); //TODO debug
		if(response != null && response.getStatus() == 200) {
			JsonObject payload = response.getJsonObject();
			JsonElement data = payload.get("data");
			if(data != null && data.isJsonObject()) {
				return data.getAsJsonObject();
			} else {
				//TODO Malformed response
			}
		} else {
			//TODO Handle request failed
		}

		return null;
	}

	/**
	 * Get a single record for this model based on its ID.
	 * @param id
	 * @return an instance of the model, null if not found
	 * 
	 * @throws IllegalArgumentException if id is not positive.
	 */
	public T getById(int id) {
		if(id < 1) throw new IllegalArgumentException("Requested resource's ID must be positive. " + id + " given.");

		RestRequest request = new RestRequest(httpClient, getUrl(id)).context(httpContext);

		RestResponse response = request.execute(HttpMethod.GET);
		if(response != null && response.getStatus() == 200) {
			JsonObject payload = response.getJsonObject();
			JsonElement data = payload.get("data");
			if(data != null && data.isJsonObject()) {
				return parseObject(data.getAsJsonObject());
			} else {
				//TODO Malformed response
			}
		} else {
			//TODO Handle request failed
		}
		return null;
	}

	/**
	 * Generate the URL based on the Host in the config and the resource name
	 * @return the url to make a request for this model
	 */
	private final String getUrl() {
		String host = Config.getInstance().get("Host");
		if(host == null) throw new NullPointerException("Host is undefined");

		return host + "/api/" + getResourceName();
	}

	/**
	 * Generate the URL based on the Host in the config, the resource name and the ID of the resource
	 * @param id - the id of the resource, must be positive
	 * @return the full url to make a request for this model and resource
	 */
	private final String getUrl(int id) {
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

}
