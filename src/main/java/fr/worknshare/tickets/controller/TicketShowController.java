package fr.worknshare.tickets.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXTextArea;

import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.networking.RequestCallback;
import fr.worknshare.tickets.networking.RestResponse;
import fr.worknshare.tickets.repository.EmployeeRepository;
import fr.worknshare.tickets.repository.FailCallback;
import fr.worknshare.tickets.repository.PaginatedRequestCallback;
import fr.worknshare.tickets.repository.TicketRepository;
import fr.worknshare.tickets.view.StatusComboBoxMaker;
import fr.worknshare.tickets.view.StatusItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Controller used for ticket creation
 * 
 * @author Jérémy LAMBERT
 *
 */
public class TicketShowController extends Controller implements Authorizable, Backable {

	private TicketRepository ticketRepository;
	private EmployeeRepository employeeRepository;
	private Ticket ticket;
	private Employee noneEmployee;
	private int currentStatus;
	private Employee currentAssignedEmployee;

	private ObservableList<Employee> employeeItems; 

	@FXML private VBox pane;
	@FXML private Label header;
	@FXML private Label createdAtLabel;
	@FXML private Label updatedAtLabel;
	@FXML private Label employeeSrcLabel;
	@FXML private JFXButton back;
	@FXML private JFXButton equipmentButton;
	@FXML private JFXTextArea description;
	@FXML private JFXComboBox<Employee> employeeAssigned;
	@FXML private JFXComboBox<StatusItem> statusBox;

	private Pane backPanel;

	private void initEmployeeAssignedCellFactory() {
		employeeAssigned.setCellFactory((lv) -> {
			return new ListCell<Employee>() {
				@Override
				protected void updateItem(Employee item, boolean empty) {
					super.updateItem(item, empty);

					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						setText(item.toString());
						setGraphic(null);
					}
				}
			};
		});
	}
	
	@FXML
	private void initialize() {
		StatusComboBoxMaker.make(statusBox);
		
		initEmployeeAssignedCellFactory();
		noneEmployee = new Employee(0);
		noneEmployee.setSurname("Aucun");
		noneEmployee.setName("");
		employeeItems = FXCollections.observableArrayList();
		employeeAssigned.setItems(employeeItems);
	}

	@FXML
	public void backClicked() {
		if(backPanel != null)
			backPanel.toFront();
	}

	@FXML
	private void onStatusChanged() {
		if(currentStatus != -1) {
			statusBox.setDisable(true);
			int status = statusBox.getSelectionModel().getSelectedItem().getStatus();
			if(currentStatus != status) {
				ticketRepository.updateStatus(ticket, status, new RequestCallback() {

					@Override
					public void run() {
						handleStatusChangedResponse(getResponse(), status);
					}
				});
			}
		}
	}
	
	private void handleStatusChangedResponse(RestResponse response, int status) {
		if(response.getStatus() == 204) {
			ticket.setStatus(status);
			currentStatus = ticket.getStatus().get();
			statusBox.setDisable(false);
			getSnackbar().enqueue(new SnackbarEvent("Le statut du ticket a été modifié avec succès.", "success"));
			return;
		} else if(response.getStatus() == 403) {
			getSnackbar().enqueue(new SnackbarEvent("Vous n'êtes pas autorisé à faire cela.", "error"));
			Logger.getGlobal().log(Level.WARNING, "Ticket status update request returned 403 status code.");
		} else {
			JsonObject object = response.getJsonObject();
			if(object.has("errors")) {
				JsonObject errors = object.get("errors").getAsJsonObject();
				if(errors.has("status"))
					getSnackbar().enqueue(new SnackbarEvent(errors.get("status").getAsString(), "error"));
			} else {
				JsonElement element = object.get("error");
				if(element != null && element.isJsonPrimitive()) {
					getSnackbar().enqueue(new SnackbarEvent(response.getStatus() + " : " + element.getAsString(), "error"));
					Logger.getGlobal().log(Level.SEVERE, "Ticket status update request failed.\n\tStatus code " + response.getStatus() + "\n\tMessage: " + element.getAsString());
				} else {
					getSnackbar().enqueue(new SnackbarEvent("Erreur " + response.getStatus(), "error"));
					Logger.getGlobal().log(Level.SEVERE, "Ticket status update request failed.\n\tStatus code " + response.getStatus());
				}
			}
		}

		//Restore status to previous one
		currentStatus = -1;
		statusBox.getSelectionModel().select(ticket.getStatus().get());
		currentStatus = ticket.getStatus().get();
		statusBox.setDisable(false);
	}

	@FXML
	private void onEmployeeAssignedChanged() {
		if(currentAssignedEmployee != null) {
			employeeAssigned.setDisable(true);
			Employee employee = employeeAssigned.getSelectionModel().getSelectedItem();
			if(currentAssignedEmployee != employee) {
				ticketRepository.updateEmployeeAssigned(ticket, employee, new RequestCallback() {

					@Override
					public void run() {
						handleEmployeeAssignedChangedResponse(getResponse(), employee);						
					}
				});
			}
		}
	}

	private void handleEmployeeAssignedChangedResponse(RestResponse response, Employee employee) {
		if(response.getStatus() == 204) {
			ticket.setEmployeeAssigned(employee == noneEmployee ? null : employee);
			currentAssignedEmployee = ticket.getEmployeeAssigned();
			employeeAssigned.setDisable(false);
			getSnackbar().enqueue(new SnackbarEvent("Le technicien a été assigné à ce ticket avec succès.", "success"));
			return;
		} else if(response.getStatus() == 403) {
			getSnackbar().enqueue(new SnackbarEvent("Vous n'êtes pas autorisé à faire cela.", "error"));
			Logger.getGlobal().log(Level.WARNING, "Ticket assign request returned 403 status code.");
		} else {
			JsonObject object = response.getJsonObject();
			if(object.has("errors")) {
				JsonObject errors = object.get("errors").getAsJsonObject();
				if(errors.has("employee"))
					getSnackbar().enqueue(new SnackbarEvent(errors.get("employee").getAsString(), "error"));
			} else {
				JsonElement element = object.get("error");
				if(element != null && element.isJsonPrimitive()) {
					getSnackbar().enqueue(new SnackbarEvent(response.getStatus() + " : " + element.getAsString(), "error"));
					Logger.getGlobal().log(Level.SEVERE, "Ticket assign request failed.\n\tStatus code " + response.getStatus() + "\n\tMessage: " + element.getAsString());
				} else {
					getSnackbar().enqueue(new SnackbarEvent("Erreur " + response.getStatus(), "error"));
					Logger.getGlobal().log(Level.SEVERE, "Ticket assign request failed.\n\tStatus code " + response.getStatus());
				}
			}
		}

		//Restore employee to previous one
		Employee previousAssignedEmployee = ticket.getEmployeeAssigned() == null ? noneEmployee : ticket.getEmployeeAssigned();
		currentAssignedEmployee = null;
		employeeAssigned.getSelectionModel().select(previousAssignedEmployee);
		currentAssignedEmployee = previousAssignedEmployee;
		employeeAssigned.setDisable(false);
	}
	
	@FXML
	private void equipmentButtonClicked() {
		//TODO show equipment panel
	}

	public void showTicket(Ticket ticket) {
		this.ticket = ticket;
		currentStatus = -1;
		currentAssignedEmployee = null;
		Employee employeeSrc = ticket.getEmployeeSource();
		Employee employeeAssigned = ticket.getEmployeeAssigned();

		header.setText("Ticket n°" + ticket.getId().get());
		createdAtLabel.setText(ticket.getCreatedAt().get());
		updatedAtLabel.setText(ticket.getUpdatedAt().get());
		employeeSrcLabel.setText(employeeSrc.getSurname().get() + " " + employeeSrc.getName().get());
		equipmentButton.setText(ticket.getEquipment().getName().get());
		description.setText(ticket.getDescription().get());
		statusBox.getSelectionModel().select(ticket.getStatus().get());
		this.employeeAssigned.getSelectionModel().select(employeeAssigned != null ? employeeAssigned : noneEmployee);

		pane.toFront();
		currentStatus = ticket.getStatus().get();
		currentAssignedEmployee = employeeAssigned == null ? noneEmployee : employeeAssigned;
	}

	public final TicketRepository getTicketRepository() {
		return ticketRepository;
	}

	public final void setTicketRepository(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	public final void setEmployeeRepository(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	/**
	 * Set the panel which will be brought to front when the "back" button is clicked
	 * @param pane
	 */
	public final void setBackPanel(Pane pane) {
		this.backPanel = pane;
	}

	@Override
	public void updateAuthorizations(int role) {
		statusBox.setDisable(role != 1 && role != 4);
		employeeAssigned.setDisable(role > 2);
		currentStatus = -1;
		currentAssignedEmployee = null;
	}

	public void updateEmployees() {
		employeeItems.clear();

		employeeRepository.getTechnicians(new PaginatedRequestCallback<Employee>() {

			@Override
			public void run() {
				employeeItems.add(noneEmployee);
				employeeItems.addAll(getPaginatedResponse().getItems());
			}
		},
				new FailCallback() {

			@Override
			public void run() {
				getSnackbar().enqueue(new SnackbarEvent(getFullMessage(), "error"));
			}
		});
	}

}
