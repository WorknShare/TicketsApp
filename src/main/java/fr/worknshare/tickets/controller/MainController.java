package fr.worknshare.tickets.controller;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;

import fr.worknshare.tickets.repository.EmployeeRepository;
import fr.worknshare.tickets.repository.EquipmentRepository;
import fr.worknshare.tickets.repository.EquipmentTypeRepository;
import fr.worknshare.tickets.repository.TicketRepository;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller used for the navigation
 * 
 * @author Jérémy LAMBERT
 *
 */
public class MainController extends Controller {

	@FXML private JFXButton logout;
	@FXML private StackPane pane;

	@FXML private JFXButton menuTickets;
	@FXML private VBox tickets;

	@FXML private FlowPane login;

	@FXML private AuthController loginController;
	@FXML private TicketsController ticketsController;
	@FXML private TicketCreateController ticketCreateController;
	@FXML private TicketShowController ticketShowController;

	private HttpClient client = HttpClientBuilder.create().build();
	private HttpContext context = new BasicHttpContext();

	private EmployeeRepository employeeRepository;
	private EquipmentRepository equipmentRepository;
	private EquipmentTypeRepository equipmentTypeRepository;
	private TicketRepository ticketRepository;

	private void initRepositories() {
		employeeRepository = new EmployeeRepository(client, context);
		equipmentRepository = new EquipmentRepository(client, context);
		equipmentTypeRepository = equipmentRepository.getEquipmentTypeRepository();
		ticketRepository = new TicketRepository(client, context, employeeRepository, equipmentRepository);

		loginController.setEmployeeRepository(employeeRepository);
		ticketsController.setTicketRepository(ticketRepository);
		ticketCreateController.setTicketRepository(ticketRepository);
		ticketShowController.setTicketRepository(ticketRepository);
		ticketShowController.setEmployeeRepository(employeeRepository);
	}

	@FXML
	private void initialize() {

		setSnackbar(new JFXSnackbar(pane));

		CookieStore cookieStore = new BasicCookieStore();		
		context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

		initRepositories();

		loginController.setHttpClient(client);
		loginController.setHttpContext(context);
		loginController.attempt("admin@worknshare.fr", "password"); //TODO disable auto auth

		loginController.setSnackbar(getSnackbar());
		loginController.setOnLogin(() -> {
			ticketsController.refresh();
			ticketShowController.updateAuthorizations();
			ticketShowController.updateEmployees();
		});
		loginController.setOnLogout(() -> {
			employeeRepository.clearCache();
			equipmentRepository.clearCache();
			equipmentTypeRepository.clearCache();
			ticketRepository.clearCache();
			
			tickets.toFront();
			login.toFront();
		});

		ticketsController.setSnackbar(getSnackbar());
		ticketsController.setHttpClient(client);
		ticketsController.setHttpContext(context);
		ticketsController.setTicketShowController(ticketShowController);

		ticketCreateController.setSnackbar(getSnackbar());
		ticketCreateController.setTicketCreatedCallback(() -> {
			tickets.toFront();
			ticketsController.setPage(1);
			ticketsController.resetFilter();
			ticketsController.refresh();
		});

		ticketShowController.setBackPanel(tickets);
		ticketShowController.setSnackbar(getSnackbar());
	}

	@FXML
	public void logoutClicked() {
		loginController.logout(logout);
	}

	@FXML
	public void onMenuTicketsClicked() {
		tickets.toFront();
		ticketsController.setPage(1);
		ticketsController.refresh();
		menuTickets.getStyleClass().add("active");
	}

	public HttpClient getHttpClient() {
		return client;
	}

	public HttpContext getContext() {
		return context;
	}
}
