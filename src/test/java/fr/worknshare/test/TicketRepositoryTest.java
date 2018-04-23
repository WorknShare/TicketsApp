package fr.worknshare.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.StringJoiner;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.repository.TicketRepository;

public class TicketRepositoryTest {

	private TicketRepository ticketRepository;
	private HttpClient client;
	private HttpContext context;

	@Before
	public void setUp() {
		client = HttpClientBuilder.create().build();
		context = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();		
		context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		ticketRepository = new TicketRepository(client, context);
	}

	@Test
	public void testParseFullNull() {

		clearCache();

		//Fully null
		JsonObject obj = prepareJson(null, null, null, null, null, null, null, null, null, null, null);
		Ticket ticket = ticketRepository.parseObject(obj);

		assertNull(ticket);

	}

	@Test
	public void testParseOnlyId() {
		clearCache();

		JsonObject obj = prepareJson("1", null, null, null, null, null, null, null, null, null, null);
		Ticket ticket = ticketRepository.parseObject(obj);
		assertNotNull(ticket);
		assertEquals(1, ticket.getId().get());
	}

	@Test
	public void testPartial() {
		clearCache();

		//Some things but no id
		JsonObject obj = prepareJson(null, "0", "description", null, null, null, null, null, null, null, null);
		Ticket ticket = ticketRepository.parseObject(obj);
		assertNull(ticket);

		//Partially filled
		obj = prepareJson("2", "0", null, "2018-02-03 18:25:42", null, "1", "John", "Michaels", null, null, null);
		ticket = ticketRepository.parseObject(obj);
		assertNotNull(ticket);
		assertEquals(2, ticket.getId().get());
		assertEquals(0, ticket.getStatus().get());
		assertEquals("03/02/2018 18:25:42", ticket.getCreatedAt().get());
		assertNull(ticket.getUpdatedAt().get());
		assertEquals(1, ticket.getIdEmployeeSource().get());
		assertNotNull(ticket.getEmployeeSource());
		assertEquals(1, ticket.getEmployeeSource().getId().get());
		assertEquals("John", ticket.getEmployeeSource().getName().get());
		assertEquals("Michaels", ticket.getEmployeeSource().getSurname().get());

		assertEquals(0, ticket.getIdEmployeeAssigned().get());
		assertNull(ticket.getEmployeeAssigned());
	}

	@Test
	public void testFull() {
		clearCache();

		JsonObject obj = prepareJson("2", "0", "Lorem ipsum", "2018-02-03 18:25:42", "2018-02-03 18:26:43", "1", "John", "Michaels", "2", "Bryan", "Gregor");
		Ticket ticket = ticketRepository.parseObject(obj);
		assertNotNull(ticket);
		assertEquals(2, ticket.getId().get());
		assertEquals(0, ticket.getStatus().get());
		assertEquals("03/02/2018 18:25:42", ticket.getCreatedAt().get());
		assertEquals("03/02/2018 18:26:43", ticket.getUpdatedAt().get());
		assertEquals(1, ticket.getIdEmployeeSource().get());
		assertNotNull(ticket.getEmployeeSource());
		assertEquals(1, ticket.getEmployeeSource().getId().get());
		assertEquals("John", ticket.getEmployeeSource().getName().get());
		assertEquals("Michaels", ticket.getEmployeeSource().getSurname().get());

		assertEquals(2, ticket.getIdEmployeeAssigned().get());
		assertNotNull(ticket.getEmployeeAssigned());
		assertEquals(2, ticket.getEmployeeAssigned().getId().get());
		assertEquals("Bryan", ticket.getEmployeeAssigned().getName().get());
		assertEquals("Gregor", ticket.getEmployeeAssigned().getSurname().get());
	}

	@Test
	public void testWrongType() {
		clearCache();
		
		boolean exception = false;
		Ticket ticket = null;
		JsonObject obj = prepareJson("id", null, null, null, null, null, null, null, null, null, null);
		try {
			ticket = ticketRepository.parseObject(obj);
		} catch(NumberFormatException nfe) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ticket);

		exception = false;
		ticket = null;
		obj = prepareJson("3", "status", "Lorem ipsum", "2018-02-03 18:25:42", "2018-02-03 18:26:43", "1", "John", "Michaels", "2", "Bryan", "Gregor");
		try {
			ticket = ticketRepository.parseObject(obj);
		} catch(NumberFormatException nfe) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ticket);
	}

	@Test
	public void testWrongDate() {
		clearCache();
		
		JsonObject obj = prepareJson("1", null, null, "2018-02-03", null, null, null, null, null, null, null);
		Ticket ticket = ticketRepository.parseObject(obj);
		assertNotNull(ticket);
		assertNull(ticket.getCreatedAt().get());
	}

	private void clearCache() {
		ticketRepository.clearCache();
		ticketRepository.getEmployeeRepository().clearCache();
		ticketRepository.getEquipmentRepository().clearCache();
	}

	/**
	 * Prepare a json object for a simulated ticket.
	 */
	private JsonObject prepareJson(String id, String status, String description, String createdAt, String updatedAt, String idEmployeeSource, String nameEmployeeSource, String surnameEmployeeSource, String idEmployeeAssigned, String nameEmployeeAssigned, String surnameEmployeeAssigned) {
		StringJoiner joiner = new StringJoiner(","); //Joiner for members
		String json = "{"; //Object start		

		if(id != null) joiner.add("\"id_ticket\":" + id);
		if(status != null) joiner.add("\"status\":" + status);
		if(description != null) joiner.add("\"description\":\"" + description + "\"");
		if(createdAt != null) joiner.add("\"created_at\":\"" + createdAt + "\"");
		if(updatedAt != null) joiner.add("\"updated_at\":\"" + updatedAt + "\"");

		if(idEmployeeSource != null) {
			StringJoiner joinerEmployeeSrc = new StringJoiner(",");
			String employeeSrc = "\"employee_source\":{";

			joinerEmployeeSrc.add("\"id_employee\":" + idEmployeeSource);
			if(nameEmployeeSource != null) joinerEmployeeSrc.add("\"name\":\"" + nameEmployeeSource + "\"");
			if(surnameEmployeeSource != null) joinerEmployeeSrc.add("\"surname\":\"" + surnameEmployeeSource + "\"");

			employeeSrc += joinerEmployeeSrc.toString();
			employeeSrc += "}";
			joiner.add(employeeSrc);

			joiner.add("\"id_employee_src\":" + idEmployeeSource);
		}

		if(idEmployeeAssigned != null) {
			StringJoiner joinerEmployeeAssigned = new StringJoiner(",");
			String employeeAssigned = "\"employee_assigned\":{";

			joinerEmployeeAssigned.add("\"id_employee\":" + idEmployeeAssigned);
			if(nameEmployeeAssigned != null) joinerEmployeeAssigned.add("\"name\":\"" + nameEmployeeAssigned + "\"");
			if(surnameEmployeeAssigned != null) joinerEmployeeAssigned.add("\"surname\":\"" + surnameEmployeeAssigned + "\"");

			employeeAssigned += joinerEmployeeAssigned.toString();
			employeeAssigned += "}";
			joiner.add(employeeAssigned);

			joiner.add("\"id_employee_assigned\":" + idEmployeeAssigned);
		}

		json += joiner.toString();
		json += "}"; //Object end
		return new JsonParser().parse(json).getAsJsonObject();
	}


}
