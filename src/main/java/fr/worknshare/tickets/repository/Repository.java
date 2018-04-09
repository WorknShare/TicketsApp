package fr.worknshare.tickets.repository;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.Config;
import fr.worknshare.tickets.model.Model;
import fr.worknshare.tickets.networking.HttpMethod;
import fr.worknshare.tickets.networking.RestRequest;
import fr.worknshare.tickets.networking.RestResponse;

/**
 * Data controller for models. Requests, retrieve and send data to the REST API.
 * 
 * @author Jérémy LAMBERT
 *
 */
public abstract class Repository<T extends Model<T>> {


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
	 * @return a list of records
	 */
	public final ArrayList<T> paginate(int page) {
		return getRequest("page", page);
	}

	/**
	 * Request the list of records corresponding to the "search" pattern.
	 * @return a list of records
	 */
	public final ArrayList<T> where(String search) {
		return getRequest("search", search);
	}

	/**
	 * Executes a simple array request using the GET method with a single parameter and returns the result.
	 * @param paramName - the name of the parameter
	 * @param paramValue - the value of the parameter
	 * @return a list of records
	 */
	private final ArrayList<T> getRequest(String paramName, Object paramValue) {
		RestRequest request = new RestRequest(getUrl())
				.setUrlParam(true)
				.param(paramName, paramValue);

		RestResponse response = request.execute(HttpMethod.GET);
		if(response != null && response.getStatus() == 200) {
			JsonObject payload = response.getJsonObject();
			JsonElement data = payload.get("data");
			if(data != null && data.isJsonArray()) {
				return parseArray(data.getAsJsonArray());
			} else {
				//Malformed response
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

		RestRequest request = new RestRequest(getUrl(id));

		RestResponse response = request.execute(HttpMethod.GET);
		if(response != null && response.getStatus() == 200) {
			JsonObject payload = response.getJsonObject();
			JsonElement data = payload.get("data");
			if(data != null && data.isJsonObject()) {
				return parseObject(data.getAsJsonObject());
			} else {
				//Malformed response
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

}
