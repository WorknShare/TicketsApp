package fr.worknshare.tickets.controller;

import java.util.logging.Logger;

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
import fr.worknshare.tickets.repository.SiteRepository;
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
	@FXML private JFXButton menuEquipments;
	@FXML private VBox tickets;
	@FXML private VBox equipments;
	@FXML private VBox equipmentShow;

	@FXML private FlowPane login;

	@FXML private AuthController loginController;
	@FXML private TicketsController ticketsController;
	@FXML private TicketCreateController ticketCreateController;
	@FXML private TicketShowController ticketShowController;
	@FXML private EquipmentController equipmentsController;
	@FXML private EquipmentShowController equipmentShowController;

	private HttpClient client;
	private HttpContext context;

	private EmployeeRepository employeeRepository;
	private EquipmentRepository equipmentRepository;
	private EquipmentTypeRepository equipmentTypeRepository;
	private TicketRepository ticketRepository;
	private SiteRepository siteRepository;

	private void initRepositories() {
		siteRepository = new SiteRepository(client, context);
		employeeRepository = new EmployeeRepository(client, context);
		equipmentRepository = new EquipmentRepository(client, context, siteRepository);
		equipmentTypeRepository = equipmentRepository.getEquipmentTypeRepository();
		ticketRepository = new TicketRepository(client, context, employeeRepository, equipmentRepository);

		loginController.setEmployeeRepository(employeeRepository);
		ticketsController.setTicketRepository(ticketRepository);
		ticketCreateController.setTicketRepository(ticketRepository);
		ticketShowController.setTicketRepository(ticketRepository);
		ticketShowController.setEmployeeRepository(employeeRepository);
		ticketShowController.setEquipmentShowController(equipmentShowController);
		equipmentsController.setEquipmentRepository(equipmentRepository);
		equipmentsController.setEquipmentTypeRepository(equipmentTypeRepository);
		equipmentsController.setEquipmentShowController(equipmentShowController);
		equipmentShowController.setTicketShowController(ticketShowController);
		equipmentShowController.setTicketCreateController(ticketCreateController);
		equipmentShowController.setTicketRepository(ticketRepository);
	}

	private void initLoginController() {
		loginController.setHttpClient(client);
		loginController.setHttpContext(context);

		loginController.setSnackbar(getSnackbar());
		loginController.setOnLogin(() -> {
			int role = loginController.getEmployee().getRole().get();
			ticketsController.updateAuthorizations(role);
			ticketShowController.updateAuthorizations(role);
			equipmentShowController.updateAuthorizations(role);
			ticketShowController.updateEmployees();
			ticketsController.setPage(1);
			ticketsController.resetFilter();
			ticketsController.refresh();
			equipmentsController.updateSite();
			equipmentsController.setPage(1);
			equipmentsController.resetFilter();
		});
		loginController.setOnLogout(() -> {
			Logger.getGlobal().info("Clearing cache...");
			employeeRepository.clearCache();
			equipmentRepository.clearCache();
			equipmentTypeRepository.clearCache();
			ticketRepository.clearCache();

			tickets.toFront();
			login.toFront();
			menuTickets.getStyleClass().add("active");
			menuEquipments.getStyleClass().remove("active");
		});
	}

	private void initTicketControllers() {
		ticketsController.setSnackbar(getSnackbar());
		ticketsController.setTicketShowController(ticketShowController);

		ticketCreateController.setSnackbar(getSnackbar());
		ticketCreateController.setTicketCreatedCallback(() -> {
			equipmentShow.toFront();
			equipmentShowController.setPage(1);
			equipmentShowController.refresh();
		});

		ticketShowController.setSnackbar(getSnackbar());
	}

	@FXML
	private void initialize() {

		setSnackbar(new JFXSnackbar(pane));

		client = HttpClientBuilder.create().build();
		context = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();		
		context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

		initRepositories();
		initLoginController();		
		initTicketControllers();
		
		equipmentsController.setSnackbar(getSnackbar());
		equipmentShowController.setSnackbar(getSnackbar());
		
		menuTickets.getStyleClass().add("active");
	}

	@FXML
	public void logoutClicked() {
		loginController.logout(logout);
	}

	@FXML
	public void onMenuTicketsClicked() {
		tickets.toFront();
		ticketsController.setPage(1);
		ticketsController.resetFilter();
		ticketsController.refresh();
		
		if(!menuTickets.getStyleClass().contains("active"))
			menuTickets.getStyleClass().add("active");
		menuEquipments.getStyleClass().remove("active");
	}
	
	@FXML
	public void onMenuEquipmentsClicked() {
		equipments.toFront();
		equipmentsController.setPage(1);
		equipmentsController.resetFilter();
		equipmentsController.refresh();
		menuTickets.getStyleClass().remove("active");
		
		if(!menuEquipments.getStyleClass().contains("active"))
			menuEquipments.getStyleClass().add("active");
	}
	
	public HttpClient getHttpClient() {
		return client;
	}

	public HttpContext getHttpContext() {
		return context;
	}
}
