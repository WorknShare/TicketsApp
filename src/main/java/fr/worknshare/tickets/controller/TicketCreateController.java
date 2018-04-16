package fr.worknshare.tickets.controller;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextArea;

import fr.worknshare.tickets.model.Equipment;
import fr.worknshare.tickets.networking.RestRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller used for ticket creation
 * 
 * @author Jérémy LAMBERT
 *
 */
public class TicketCreateController implements RequestController {


	@FXML private JFXButton submit;
	@FXML private JFXButton back;
	@FXML private Label equipmentLabel;
	@FXML private JFXTextArea description;
	
	private JFXSnackbar snackbar;
	
	private HttpClient httpClient;
	private HttpContext httpContext;
	
	private Equipment equipment;

	protected void setSnackbar(JFXSnackbar bar) {
		this.snackbar = bar;
	}
	
	@FXML
	public void submitClicked(ActionEvent e) {
		submit();
	}
	
	private void submit() {
		description.setDisable(true);
		submit.setDisable(true);
	}

	/**
	 * Set the Http client used for auth requests
	 * @param client
	 */
	@Override
	public void setHttpClient(HttpClient client) {
		httpClient = client;
	}
	
	/**
	 * Set the Http context used for auth requests
	 * @param context
	 */
	@Override
	public void setHttpContext(HttpContext context) {
		httpContext = context;
	}
	
	public void setSelectedEquipment(Equipment equipment) {
		this.equipment = equipment;
		equipmentLabel.setText("Matériel concerné : " + equipment.getName());
		description.setText(null);
	}
}
