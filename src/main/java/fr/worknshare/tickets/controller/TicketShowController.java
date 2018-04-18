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
import fr.worknshare.tickets.view.StatusItem;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
	int currentStatus;
	int currentAssignedEmployee;
	
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

	private void initStatusBox() {

		ObservableList<StatusItem> items = FXCollections.observableArrayList();
		items.addAll(new StatusItem(0, "En attente"), 
				new StatusItem(1, "En cours"),
				new StatusItem(2, "Résolu"),
				new StatusItem(3, "Non résolu"),
				new StatusItem(4, "Invalide"));
		statusBox.setItems(items);

		statusBox.buttonCellProperty().bind(Bindings.createObjectBinding(() -> {

			StackPane arrowButton = (StackPane) statusBox.lookup(".arrow-button");

			return new ListCell<StatusItem>() {

				@Override
				protected void updateItem(StatusItem item, boolean empty) {
					super.updateItem(item, empty);

					if (empty || item == null) {
						setBackground(Background.EMPTY);
						setText("");
					} else {

						setText(item.getName());
						switch(item.getStatus()) {
						case 0:
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(0.86, 0.207, 0.27, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 1:
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(1.0, 0.756, 0.027, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 2:
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(0.157, 0.655, 0.27, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 3:
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(0.09, 0.635, 0.721, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 4:
							setTextFill(Color.BLACK);
							setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						default:
							setTextFill(Color.BLACK);
							setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
						}
					}

					if(arrowButton != null)
						arrowButton.setBackground(getBackground());
				}

			};
		}, statusBox.valueProperty()));
	}

	@FXML
	private void initialize() {
		initStatusBox();
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
			int status = statusBox.getSelectionModel().getSelectedItem().getStatus();
			if(currentStatus != status) {
				ticketRepository.updateStatus(ticket, status, new RequestCallback() {

					@Override
					public void run() {
						RestResponse response = getResponse();
						if(response.getStatus() == 204) {
							ticket.setStatus(status);
							currentStatus = ticket.getStatus().get();
							getSnackbar().enqueue(new SnackbarEvent("Le statut du ticket a été modifié avec succès.", "success"));
							return;
						} else if(response.getStatus() == 403) {
							getSnackbar().enqueue(new SnackbarEvent("Vous n'êtes pas autorisé à faire cela.", "error"));
							Logger.getGlobal().log(Level.WARNING, "Ticket status update request returned 403 status code.");
						} else {
							JsonObject object = response.getJsonObject();
							if(object.has("errors")) {
								JsonObject errors = object.get("errors").getAsJsonObject();
								if(errors.has("id_equipment"))
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
					}
				});
			}
		}
	}

	@FXML
	private void onEmployeeAssignedChanged() {
		//TODO show equipment panel
	}
	
	@FXML
	private void equipmentButtonClicked() {
		//TODO show equipment panel
	}

	public void showTicket(Ticket ticket) {
		this.ticket = ticket;
		currentStatus = -1;
		currentAssignedEmployee = -1;
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
		currentAssignedEmployee = employeeAssigned != null ? employeeAssigned.getId().get() : 0;
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
	public void updateAuthorizations() {
		int role = AuthController.getEmployee().getRole().get();
		statusBox.setDisable(role != 1 && role != 4);
		employeeAssigned.setDisable(role > 2);
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
