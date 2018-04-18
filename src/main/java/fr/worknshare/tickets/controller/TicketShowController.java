package fr.worknshare.tickets.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.repository.TicketRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Controller used for ticket creation
 * 
 * @author Jérémy LAMBERT
 *
 */
public class TicketShowController extends Controller {

	private TicketRepository ticketRepository;

	@FXML private VBox pane;
	@FXML private Label header;
	@FXML private Label createdAtLabel;
	@FXML private Label updatedAtLabel;
	@FXML private Label employeeSrcLabel;
	@FXML private Label employeeAssignedLabel;
	@FXML private JFXButton back;
	@FXML private JFXButton equipmentButton;
	@FXML private JFXTextArea description;

	private Pane backPanel;

	@FXML
	private void backClicked() {
		if(backPanel != null)
			backPanel.toFront();
	}

	@FXML
	private void equipmentButtonClicked() {
		//TODO
	}

	public void showTicket(Ticket ticket) {
		Employee employeeSrc = ticket.getEmployeeSource();
		Employee employeeAssigned = ticket.getEmployeeAssigned();

		header.setText("Ticket n°" + ticket.getId().get());
		createdAtLabel.setText(ticket.getCreatedAt().get());
		updatedAtLabel.setText(ticket.getUpdatedAt().get());
		employeeSrcLabel.setText(employeeSrc.getSurname().get() + " " + employeeSrc.getName().get());
		employeeAssignedLabel.setText(employeeAssigned != null ? employeeAssigned.getSurname().get() + " " + employeeAssigned.getName().get() : "Aucun");
		equipmentButton.setText(ticket.getEquipment().getName().get());
		description.setText(ticket.getDescription().get());
		
		pane.toFront();
	}

	public final TicketRepository getTicketRepository() {
		return ticketRepository;
	}

	public final void setTicketRepository(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	/**
	 * Set the panel which will be brought to front when the "back" button is clicked
	 * @param pane
	 */
	public final void setBackPanel(Pane pane) {
		this.backPanel = pane;
	}

}
