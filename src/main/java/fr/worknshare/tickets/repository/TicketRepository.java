package fr.worknshare.tickets.repository;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.model.Equipment;
import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.networking.HttpMethod;
import fr.worknshare.tickets.networking.RequestCallback;
import fr.worknshare.tickets.networking.RestRequest;

public final class TicketRepository extends Repository<Ticket> implements CreatorRepository<Ticket> {

	private EmployeeRepository employeeRepository;
	private EquipmentRepository equipmentRepository;

	public TicketRepository(HttpClient client, HttpContext context) {
		super(client, context);
		employeeRepository = new EmployeeRepository(client, context);
		equipmentRepository = new EquipmentRepository(client, context);
	}

	public TicketRepository(HttpClient client, HttpContext context, EmployeeRepository employeeRepository, EquipmentRepository equipmentRepository) {
		super(client, context);
		this.employeeRepository = employeeRepository;
		this.equipmentRepository = equipmentRepository;
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
			if(element != null && element.isJsonPrimitive()) { //Only ID
				ticket.setIdEmployeeSource(element.getAsInt());
			}

			element = object.get("employee_source");
			if(element != null && element.isJsonObject()) { //Employee object
				Employee employee = employeeRepository.parseObject(element.getAsJsonObject());
				if(employee != null)
					ticket.setEmployeeSource(employee);
			}

			//Employee assigned
			element = object.get("id_employee_assigned");
			if(element != null && element.isJsonPrimitive()) { //Only ID
				ticket.setIdEmployeeAssigned(element.getAsInt());
			}

			element = object.get("employee_assigned");
			if(element != null && element.isJsonObject()) { //Employee object
				Employee employee = employeeRepository.parseObject(element.getAsJsonObject());
				if(employee != null)
					ticket.setEmployeeAssigned(employee);
			}

			//Equipment
			element = object.get("equipment");
			if(element != null && element.isJsonObject()) {
				Equipment equipment = equipmentRepository.parseObject(element.getAsJsonObject());
				ticket.setEquipment(equipment);
				ticket.setIdEquipment(equipment.getId().get());

				//Type
				if(equipment != null) ticket.setEquipmentType(equipment.getEquipmentType());
			}

			//Created at
			element = object.get("created_at");
			if(element != null && element.isJsonPrimitive()) {
				Date date;
				try {
					date = getDateFormatter().parse(element.getAsString());
					ticket.setCreatedAt(date);
				} catch (ParseException e) {
					Logger.getGlobal().log(Level.WARNING, "Unable to parse date", e);
				}
			}

			//Updated at
			element = object.get("updated_at");
			if(element != null && element.isJsonPrimitive()) {
				Date date;
				try {
					date = getDateFormatter().parse(element.getAsString());
					ticket.setUpdatedAt(date);
				} catch (ParseException e) {
					Logger.getGlobal().log(Level.WARNING, "Unable to parse date", e);
				}
			}

			return ticket;
		}
		return null;
	}

	@Override
	public void create(Ticket resource, RequestCallback callback) {

		RestRequest request = new RestRequest(getHttpClient(), getUrl())
				.setUrlParam(true)
				.context(getHttpContext())
				.param("description", resource.getDescription().get())
				.param("id_equipment", resource.getIdEquipment().get());

		request.asyncExecute(HttpMethod.POST, callback);
	}

	/**
	 * Update the status of the given ticket on the remote server
	 * @param ticket
	 */
	public void updateStatus(Ticket ticket, int status, RequestCallback callback) {
		RestRequest request = new RestRequest(getHttpClient(), getUrl(ticket.getId().get()))
				.context(getHttpContext())
				.param("status", status);

		request.asyncExecute(HttpMethod.PUT, callback);
	}

	/**
	 * Update the assigned employee for the given ticket on the remote server
	 * @param employee
	 */
	public void updateEmployeeAssigned(Ticket ticket, Employee employee, RequestCallback callback) {
		RestRequest request = new RestRequest(getHttpClient(), getUrl(ticket.getId().get()) + "/affect")
				.context(getHttpContext())
				.param("employee", employee.getId().get());

		request.asyncExecute(HttpMethod.PUT, callback);
	}

}
