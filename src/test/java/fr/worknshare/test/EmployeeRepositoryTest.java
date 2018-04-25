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

import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.repository.EmployeeRepository;

public class EmployeeRepositoryTest {

	private EmployeeRepository employeeRepository;
	private HttpClient client;
	private HttpContext context;

	@Before
	public void setUp() {
		client = HttpClientBuilder.create().build();
		context = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();		
		context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		employeeRepository = new EmployeeRepository(client, context);
	}

	@Test
	public void testParseFullNull() {

		clearCache();

		//Fully null
		JsonObject obj = prepareJson(null, null, null, null, null, null, null);
		Employee employee = employeeRepository.parseObject(obj);

		assertNull(employee);

	}

	@Test
	public void testParseOnlyId() {
		clearCache();

		JsonObject obj = prepareJson("1", null, null, null, null, null, null);
		Employee employee = employeeRepository.parseObject(obj);
		assertNotNull(employee);
		assertEquals(1, employee.getId().get());
	}

	@Test
	public void testPartial() {
		clearCache();

		//Some things but no id
		JsonObject obj = prepareJson(null, "name", "surname", null, null, null, null);
		Employee employee = employeeRepository.parseObject(obj);
		assertNull(employee);

		//Partially filled
		obj = prepareJson("2", "name", null, "email@test.fr", null, null, "1");
		employee = employeeRepository.parseObject(obj);
		assertNotNull(employee);
		assertEquals(2, employee.getId().get());
		assertEquals("name", employee.getName().get());
		assertNull(employee.getSurname().get());
		assertEquals("email@test.fr", employee.getEmail().get());
		assertNull(employee.getAddress().get());
		assertNull(employee.getPhone().get());
		assertEquals(1, employee.getRole().get());
		assertEquals("name", employee.getFullName().get());
	}

	@Test
	public void testFull() {
		clearCache();

		JsonObject obj = prepareJson("2", "John", "Michaels", "email@test.fr", "0101010101", "12 rue de l'avenue", "3");
		Employee employee = employeeRepository.parseObject(obj);
		assertNotNull(employee);
		assertEquals(2, employee.getId().get());
		assertEquals("John", employee.getName().get());
		assertEquals("Michaels", employee.getSurname().get());
		assertEquals("email@test.fr", employee.getEmail().get());
		assertEquals("0101010101", employee.getPhone().get());
		assertEquals("12 rue de l'avenue", employee.getAddress().get());
		assertEquals(3, employee.getRole().get());
		assertEquals("Michaels John", employee.getFullName().get());
	}

	@Test
	public void testWrongType() {
		clearCache();
		
		boolean exception = false;
		Employee employee = null;
		JsonObject obj = prepareJson("id", null, null, null, null, null, null);
		try {
			employee = employeeRepository.parseObject(obj);
		} catch(NumberFormatException nfe) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(employee);

		exception = false;
		employee = null;
		obj = prepareJson("3", "name", "surname", "email", "phone", "address", "role");
		try {
			employee = employeeRepository.parseObject(obj);
		} catch(NumberFormatException nfe) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(employee);
	}

	private void clearCache() {
		employeeRepository.clearCache();
	}

	/**
	 * Prepare a json object for a simulated employee.
	 */
	private JsonObject prepareJson(String id, String name, String surname, String email, String phone, String address, String role) {
		StringJoiner joiner = new StringJoiner(","); //Joiner for members
		String json = "{"; //Object start		

		if(id != null) joiner.add("\"id_employee\":" + id);
		if(name != null) joiner.add("\"name\":\"" + name + "\"");
		if(surname != null) joiner.add("\"surname\":\"" + surname + "\"");
		if(phone != null) joiner.add("\"phone\":\"" + phone + "\"");
		if(address != null) joiner.add("\"address\":\"" + address + "\"");
		if(email != null) joiner.add("\"email\":\"" + email + "\"");
		if(role != null) joiner.add("\"role\":" + role);

		json += joiner.toString();
		json += "}"; //Object end
		return new JsonParser().parse(json).getAsJsonObject();
	}


}
