package fr.worknshare.tickets.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.Config;
import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.networking.HttpMethod;
import fr.worknshare.tickets.networking.RestRequest;
import fr.worknshare.tickets.networking.RestResponse;
import fr.worknshare.tickets.repository.EmployeeRepository;

/**
 * Controller used for authentication
 * 
 * @author Jérémy LAMBERT
 *
 */
public class AuthController extends Controller {

	private EmployeeRepository employeeRepository;
	private Employee employee;
	
	public AuthController() {
		this.employeeRepository = new EmployeeRepository();
	}
	
	/**
	 * Attempts to authenticate the user. Stores the authenticated Employee into the controller on success.
	 * @param email - the user's email
	 * @param password - the uses's password
	 * @return true if the user successfully authenticated
	 * 
	 * @see Employee
	 */
	public boolean attempt(String email, String password) {
		RestRequest request = new RestRequest(getUrl() + "login")
				.param("email", email)
				.param("password", password);
		
		RestResponse response = request.execute(HttpMethod.POST);
		if(response != null && response.getStatus() == 200) {
			JsonObject payload = response.getJsonObject();
			JsonElement data = payload.get("data");
			if(data != null && data.isJsonObject()) {
				this.employee = employeeRepository.parseObject(data.getAsJsonObject());
				return true;
			} else {
				//Malformed response
				Logger.getGlobal().log(Level.INFO, "Login response malformed:\n" + response.getRaw());
			}
		} else {
			//TODO Handle request failed
			//422 -> invalid credentials
			Logger.getGlobal().log(Level.INFO, "Login request failed. Status code " + response.getStatus());
		}
		return false;
	}
	
	/**
	 * Send a logout request. Sets the previously authenticated employee to null
	 * @return true on successful logout
	 */
	public boolean logout() {
		RestRequest request = new RestRequest(getUrl() + "logout");
		
		RestResponse response = request.execute(HttpMethod.POST);
		if(response != null && response.getStatus() == 200) {
			employee = null;
			return true;
		} else {
			//TODO Handle request failed
			Logger.getGlobal().log(Level.INFO, "Logout request failed. Status code " + response.getStatus());
		}
		return false;
	}
	
	/**
	 * Generate the URL based on the Host in the config and the resource name
	 * @return the url to make a request for this model
	 */
	private final String getUrl() {
		String host = Config.getInstance().get("Host");
		if(host == null) throw new NullPointerException("Host is undefined");

		return host + "/api/";
	}
	
	public Employee getEmployee() {
		return this.employee;
	}
}
