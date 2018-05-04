package fr.worknshare.tickets.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;

import fr.worknshare.tickets.model.Equipment;
import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.view.TicketTableMaker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class EquipmentShowController implements Backable, Authorizable {

	@FXML VBox pane;
	@FXML Label serialLabel;
	@FXML Label typeLabel;
	@FXML Label siteLabel;
	@FXML JFXTreeTableView<Ticket> table;
	@FXML JFXButton createTicket;
	@FXML JFXButton back;

	private Equipment equipment;

	private ObservableList<Ticket> ticketList;
	private Pane backPanel;
	private TicketShowController ticketShowController;
	private TicketCreateController ticketCreateController;

	@FXML
	private void initialize() {
		ticketList = FXCollections.observableArrayList();
		TicketTableMaker.make(table, ticketList);
		initDoubleClickListener();
	}

	private void initDoubleClickListener() {
		table.setRowFactory( tv -> {
			TreeTableRow<Ticket> row = new TreeTableRow<Ticket>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					Ticket ticket = row.getItem();
					ticketShowController.setBackPanel(pane);
					ticketShowController.showTicket(ticket);
				}
			});
			return row;
		});
	}

	public void showEquipment(Equipment equipment) {
		this.equipment = equipment;
		serialLabel.setText(equipment.getName().get());
		typeLabel.setText(equipment.getEquipmentType().getName().get());
		siteLabel.setText(equipment.getSite() != null ? equipment.getSite().getName().get() : "Aucun");
		pane.toFront();
	}

	@Override
	public void updateAuthorizations(int role) {
		createTicket.setVisible(role < 4 && role > 0);
	}

	@Override
	public void setBackPanel(Pane pane) {
		this.backPanel = pane;
	}

	@FXML
	public void backClicked() {
		if(backPanel != null)
			backPanel.toFront();
	}

	@FXML
	public void createClicked() {
		ticketCreateController.setBackPanel(pane);
		ticketCreateController.setSelectedEquipment(equipment);
		ticketCreateController.show();
	}

	public void setTicketShowController(TicketShowController ticketShowController) {
		this.ticketShowController = ticketShowController;
	}

	public void setTicketCreateController(TicketCreateController ticketCreateController) {
		this.ticketCreateController = ticketCreateController;
	}

}
