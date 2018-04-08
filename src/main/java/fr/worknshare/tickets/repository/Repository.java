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
public abstract class Repository<T extends Model> {


	/**
	 * Get the name of the resource. Will be used in URLs when doing requests.
	 * <br>
	 * For example, a resource having the name "user" will request the REST API to "http://host/api/user" automatically.
	 * @return the name of the resource
	 */
	public abstract String getResourceName();

	/**
	 * Request the list of records corresponding to the "search" pattern.
	 * @return a list of records
	 */
	public final ArrayList<T> where(String search) {

		RestRequest request = new RestRequest(getUrl())
				.setUrlParam(true)
				.param("search", search);

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
			//TODO Handle response failed
		}

		return null;
	}
	
	/**
	 * Iterate through the response array and create a list of models
	 * @param array - the array from the response containing the objects
	 * @return a list of models with filled values
	 */
	private ArrayList<T> parseArray(JsonArray array) {
		ArrayList<T> list = new ArrayList<T>();
		array.forEach((element) -> {
			list.add(parseObject(element));
		});
		return list;
	}
	
	/**
	 * Create an instance of the model based on the given JsonElement.
	 * @param element - the element from the response
	 * @return an instance of the model with filled values
	 */
	protected abstract T parseObject(JsonElement element);

	public T getById(int id) {
		if(id < 1) throw new IllegalArgumentException("Requested resource's ID must be positive. " + id + " given.");
		return null;
	}

	private final String getUrl() {
		String host = Config.getInstance().get("Host");
		if(host == null) throw new NullPointerException("Host is undefined");

		return host + "/" + getResourceName();
	}

	private final String getUrl(int id) {
		return getUrl() + "/" + id;
	}

}
