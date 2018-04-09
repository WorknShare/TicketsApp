package fr.worknshare.tickets.repository;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.model.Ticket;

public final class TicketRepository extends Repository<Ticket> {

	private EmployeeRepository employeeRepository;
	private EquipmentRepository equipmentRepository;

	public TicketRepository() {
		employeeRepository = new EmployeeRepository();
		equipmentRepository = new EquipmentRepository();
	}

	@Override
	public String getResourceName() {
		return "ticket";
	}

	@Override
	public Ticket parseObject(JsonObject object) {

		JsonElement element = object.get("id_ticket");
		if(element != null && element.isJsonPrimitive()) {
			Ticket ticket = new Ticket(element.getAsInt());

			//Status
			element = object.get("status");
			if(element != null && element.isJsonPrimitive()) ticket.setStatus(element.getAsInt());

			//Description
			element = object.get("description");
			if(element != null && element.isJsonPrimitive()) ticket.setDescription(element.getAsString());

			//Employee source
			element = object.get("id_employee_src");
			if(element != null) {
				if(element.isJsonPrimitive()) { //Only have ID
					ticket.setIdEmployeeSource(element.getAsInt());
				} else if(element.isJsonObject()) { //Have full object (join)
					Employee employee = employeeRepository.parseObject(element.getAsJsonObject());
					if(employee != null) {
						ticket.setEmployeeSource(employee);
						ticket.setIdEmployeeSource(employee.getId().get());
					}
				}
			}
			
			//Employee assigned
			element = object.get("id_employee_assigned");
			if(element != null) {
				if(element.isJsonPrimitive()) { //Only have ID
					ticket.setIdEmployeeAssigned(element.getAsInt());
				} else if(element.isJsonObject()) { //Have full object (join)
					Employee employee = employeeRepository.parseObject(element.getAsJsonObject());
					if(employee != null) {
						ticket.setEmployeeAssigned(employee);
						ticket.setIdEmployeeAssigned(employee.getId().get());
					}
				}
			}
			
			//Equipment
			element = object.get("equipment");
			if(element != null && element.isJsonObject()) ticket.setEquipment(equipmentRepository.parseObject(element.getAsJsonObject()));
			
			//TODO equipment type

			return ticket;
		}
		return null;
	}

}
