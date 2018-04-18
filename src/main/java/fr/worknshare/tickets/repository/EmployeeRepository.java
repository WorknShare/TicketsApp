package fr.worknshare.tickets.repository;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.view.Paginator;

public final class EmployeeRepository extends Repository<Employee> {

	@Override
	public String getResourceName() {
		return "employee";
	}

	@Override
	public Employee parseObject(JsonObject object) {
		JsonElement element = object.get("id_employee");
		if(element != null && element.isJsonPrimitive()) {
			Employee employee = new Employee(element.getAsInt());

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

			//API token
			element = object.get("api_token");
			if(element != null && element.isJsonPrimitive()) employee.setToken(element.getAsString());

			return employee;
		}
		return null;
	}
	
	public void getTechnicians(PaginatedRequestCallback<Employee> callback, FailCallback failCallback) {
		request(null, null, new JsonCallback() {

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
			
		}, failCallback, getUrl() + "/tech");
	}

}
