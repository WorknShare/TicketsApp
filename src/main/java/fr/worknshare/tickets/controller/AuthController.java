package fr.worknshare.tickets.controller;

import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import fr.worknshare.tickets.Config;
import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.networking.HttpMethod;
import fr.worknshare.tickets.networking.RestRequest;
import fr.worknshare.tickets.networking.RestResponse;
import fr.worknshare.tickets.repository.EmployeeRepository;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

/**
 * Controller used for authentication
 * 
 * @author Jérémy LAMBERT
 *
 */
public class AuthController {

	private EmployeeRepository employeeRepository;
	private Employee employee;

	@FXML private JFXButton submit;
	@FXML private JFXTextField emailField;
	@FXML private JFXPasswordField passwordField;

	@FXML private Label errorsEmail;
	@FXML private Label errorsPassword;
	
	@FXML private FlowPane loginPane;

	@FXML
	private void initialize() {
		this.employeeRepository = new EmployeeRepository();

		//Submit on enter
		passwordField.setOnKeyPressed((event) -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				submit();
			}
		});
	}

	/**
	 * Attempts to authenticate the user. Stores the authenticated Employee into the controller on success.
	 * @param email - the user's email
	 * @param password - the uses's password
	 * @return true if the user successfully authenticated
	 * 
	 * @see Employee
	 */
	public boolean attempt(String email, String password) {
		RestRequest request = new RestRequest(getUrl() + "login")
				.param("email", email)
				.param("password", password);

		RestResponse response = request.execute(HttpMethod.POST);
		if(response != null) {
			if(response.getStatus() == 200) {
				JsonObject payload = response.getJsonObject();
				JsonElement data = payload.get("data");
				if(data != null && data.isJsonObject()) {
					this.employee = employeeRepository.parseObject(data.getAsJsonObject());
					return true;
				} else {
					//Malformed response
					Logger.getGlobal().log(Level.INFO, "Login response malformed:\n" + response.getRaw());
				}
			} else {

				if(response.getStatus() == 422) //Invalid credentials
					handleResponse(response.getJsonObject().get("errors").getAsJsonObject());
				else				
					Logger.getGlobal().log(Level.WARNING, "Login request failed.\n\tStatus code " + response.getStatus() + "\n\tMessage: " + response.getJsonObject().get("message").getAsString());

			}
		} else
			Logger.getGlobal().log(Level.WARNING, "Login request failed. Internal error.");


		return false;
	}

	/**
	 * Send a logout request. Sets the previously authenticated employee to null
	 * @return true on successful logout
	 */
	public boolean logout() {
		RestRequest request = new RestRequest(getUrl() + "logout");

		RestResponse response = request.execute(HttpMethod.POST);
		if(response != null)  {
			if(response.getStatus() == 200) {
				employee = null;
				FadeTransition ft = new FadeTransition(Duration.millis(800), loginPane);
				ft.setFromValue(0.0);
				ft.setToValue(1.0);
				loginPane.toFront();
				ft.play();
				return true;
			} else {
				Logger.getGlobal().log(Level.WARNING, "Login request failed.\n\tStatus code " + response.getStatus() + "\n\tMessage: " + response.getJsonObject().get("message").getAsString());
			}
		} else
			Logger.getGlobal().log(Level.WARNING, "Logout request failed. Internal error.");
		return false;
	}

	@FXML
	public void submitClicked(ActionEvent e) {
		submit();
	}
	
	private void submit() {
		
		resetInputs();

		if(attempt(emailField.getText(), passwordField.getText())) {
			FadeTransition ft = new FadeTransition(Duration.millis(800), loginPane);
			ft.setFromValue(1.0);
			ft.setToValue(0.0);
			ft.play();
			ft.setOnFinished((event) -> {
				loginPane.toBack();
				emailField.setText("");
				passwordField.setText("");
				emailField.setDisable(false);
				passwordField.setDisable(false);
			});
		} else {
			emailField.setDisable(false);
			passwordField.setDisable(false);
		}
	}
	
	private void resetInputs() {
		emailField.setDisable(true);
		passwordField.setDisable(true);
		emailField.getStyleClass().remove("has-error");
		passwordField.getStyleClass().remove("has-error");
		errorsEmail.setText("");
		errorsPassword.setText("");
		errorsEmail.setVisible(false);
		errorsPassword.setVisible(false);
	}

	/**
	 * Handle a negative response. (Show error messages on view)
	 * @param errors - the json object containing the response errors messages
	 */
	private void handleResponse(JsonObject errors) {
		if(errors.has("email")) {
			emailField.getStyleClass().add("has-error");
			StringJoiner joiner = new StringJoiner("\n");
			errors.get("email").getAsJsonArray().forEach((elem) -> {
				joiner.add(elem.getAsString());
			});
			errorsEmail.setText(joiner.toString());
			errorsEmail.setVisible(true);
		}

		if(errors.has("password")) {
			passwordField.getStyleClass().add("has-error");
			StringJoiner joiner = new StringJoiner("\n");
			errors.get("password").getAsJsonArray().forEach((elem) -> {
				joiner.add(elem.getAsString());
			});
			errorsPassword.setText(joiner.toString());
			errorsPassword.setVisible(true);
		}
	}

	/**
	 * Generate the URL based on the Host in the config and the resource name
	 * @return the url to make a request for this model
	 */
	private final String getUrl() {
		String host = Config.getInstance().get("Host");
		if(host == null) throw new NullPointerException("Host is undefined");

		return host + "/api/";
	}

	public Employee getEmployee() {
		return this.employee;
	}
}
