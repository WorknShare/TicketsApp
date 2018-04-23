package fr.worknshare.test;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.worknshare.tickets.repository.TicketRepository;

public class TicketRepositoryTest {

	private TicketRepository ticketRepository;
	private HttpClient client = HttpClientBuilder.create().build();
	private HttpContext context = new BasicHttpContext();
	
	@Before
	public void setUp() {
		ticketRepository = new TicketRepository(client, context);
	}
	
	@After
	public void tearDown() {
		//
	}
	
	@Test
	public void test() {
		//fail("Not implemented");
	}


}
