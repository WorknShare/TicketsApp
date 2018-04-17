package fr.worknshare.tickets.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXTextArea;

import fr.worknshare.tickets.model.Equipment;
import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.networking.RequestCallback;
import fr.worknshare.tickets.networking.RestResponse;
import fr.worknshare.tickets.repository.TicketRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller used for ticket creation
 * 
 * @author Jérémy LAMBERT
 *
 */
public class TicketCreateController {

	private TicketRepository ticketRepository;
	
	@FXML private JFXButton submit;
	@FXML private JFXButton back;
	@FXML private Label equipmentLabel;
	@FXML private JFXTextArea description;
	
	private JFXSnackbar snackbar;
	
	private Equipment equipment;

	private Runnable createdCallback;

	
	@FXML
	public void submitClicked() {
		submit();
	}
	
	private void submit() {
		String text = description.getText() != null ? description.getText().trim() : "";
		if(!text.isEmpty()) {
			
			if(equipment == null) {
				snackbar.enqueue(new SnackbarEvent("Erreur : Aucun matériel sélectionné", "error"));
				Logger.getGlobal().warning("Tried to submit a null equipment.");
				return;
			}
			
			description.setDisable(true);
			submit.setDisable(true);
			
			Ticket ticket = new Ticket(-1);
			ticket.setDescription(text);
			ticket.setIdEquipment(equipment.getId().get());
			
			ticketRepository.create(ticket, new RequestCallback() {
				
				@Override
				public void run() {
					handleSubmitResponse(getResponse(), ticket);
				}
			});
			
		} else {
			snackbar.enqueue(new SnackbarEvent("Le champ description est obligatoire", "error"));
		}
	}
	
	private void handleSubmitResponse(RestResponse response, Ticket ticket) {
		JsonObject object = response.getJsonObject();
		JsonElement element;
		
		if(response.getStatus() == 201) { //Success
			element = object.get("id");
			if(element != null && element.isJsonPrimitive()) {
				int id = element.getAsInt();
				ticket.setId(id);
				description.setText(null);
				snackbar.enqueue(new SnackbarEvent("Le ticket n°" + id + " a été créé.", "success"));
				createdCallback.run();
			}
		} else if(object.has("errors")) {
			handleSubmitErrors(object.get("errors").getAsJsonObject());
		} else {
			element = object.get("error");
			if(element != null && element.isJsonPrimitive()) 
				snackbar.enqueue(new SnackbarEvent(response.getStatus() + " : " + element.getAsString(), "error"));
			else
				snackbar.enqueue(new SnackbarEvent("Erreur " + response.getStatus(), "error"));
			Logger.getGlobal().log(Level.SEVERE, "Ticket submit request failed.\n\tStatus code " + response.getStatus() + "\n\tMessage: " + element.getAsString());
		}		
		
		description.setDisable(false);
		submit.setDisable(false);
	}
	
	private void handleSubmitErrors(JsonObject errors) {
		if(errors.has("id_equipment"))
			snackbar.enqueue(new SnackbarEvent(errors.get("id_equipment").getAsString(), "error"));
	}
	
	public void setSelectedEquipment(Equipment equipment) {
		this.equipment = equipment;
		equipmentLabel.setText("Matériel concerné : " + equipment.getName().get());
		description.setText(null);
	}
	
	/**
	 * Set the snackbar used to display errors and successes
	 * @param snackbar
	 */
	public void setSnackbar(JFXSnackbar snackbar) {
		this.snackbar = snackbar;
	}

	public final TicketRepository getTicketRepository() {
		return ticketRepository;
	}

	public final void setTicketRepository(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	/**
	 * Set the behavior to execute when a ticket is created
	 * @param runnable
	 */
	public void setTicketCreatedCallback(Runnable runnable) {
		this.createdCallback = runnable;
	}
	
}
