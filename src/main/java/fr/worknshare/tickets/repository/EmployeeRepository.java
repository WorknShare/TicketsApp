package fr.worknshare.tickets.repository;

import com.google.gson.JsonElement;

import fr.worknshare.tickets.model.Employee;

public final class EmployeeRepository extends Repository<Employee> {

	@Override
	public String getResourceName() {
		return "employee";
	}

	@Override
	protected Employee parseObject(JsonElement element) {
		// TODO Auto-generated method stub
		return null;
	}

}
