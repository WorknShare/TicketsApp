package fr.worknshare.tickets.repository;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.view.Paginator;

public final class EmployeeRepository extends Repository<Employee> {

	public EmployeeRepository(HttpClient client, HttpContext context) {
		super(client, context);
	}

	@Override
	public String getResourceName() {
		return "employee";
	}

	@Override
	public Employee parseObject(JsonObject object) {
		JsonElement element = object.get("id_employee");
		if(element != null && element.isJsonPrimitive()) {

			Employee employee = getFromCache(element.getAsInt());
			if(employee == null)			
				employee = new Employee(element.getAsInt());

			//Name
			element = object.get("name");
			if(element != null && element.isJsonPrimitive()) employee.setName(element.getAsString());

			//Surname
			element = object.get("surname");
			if(element != null && element.isJsonPrimitive()) employee.setSurname(element.getAsString());

			//Email
			element = object.get("email");
			if(element != null && element.isJsonPrimitive()) employee.setEmail(element.getAsString());

			//Phone
			element = object.get("phone");
			if(element != null && element.isJsonPrimitive()) employee.setPhone(element.getAsString());

			//Address
			element = object.get("address");
			if(element != null && element.isJsonPrimitive()) employee.setAddress(element.getAsString());

			//Role
			element = object.get("role");
			if(element != null && element.isJsonPrimitive()) employee.setRole(element.getAsInt());

			registerModel(employee);
			return employee;
		}
		return null;
	}

	public void getTechnicians(PaginatedRequestCallback<Employee> callback, FailCallback failCallback) {
		request(null, null, null, getUrl() + "/tech", new JsonCallback() {

			@Override
			public void run() {
				PaginatedResponse<Employee> response = null;
				ArrayList<Employee> list = null;
				JsonObject data = getObject();
				JsonElement elem = data.get("items");
				if(elem != null && elem.isJsonArray()) {
					list = parseArray(elem.getAsJsonArray());
					response = new PaginatedResponse<Employee>(new Paginator(1, 1, 10), list);
					callback.setResponse(getResponse());
					callback.setPaginatedResponse(response);
					callback.run();
				} else {
					failCallback.setResponse(getResponse());
					failCallback.setMessage("Réponse malformée");
					failCallback.run();
					Logger.getGlobal().warning("Malformed where response (missing expected \"items\"):\n\t" + getResponse().getRaw());
				}
			}

		}, failCallback);
	}

}
