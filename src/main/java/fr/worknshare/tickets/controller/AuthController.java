package fr.worknshare.tickets.controller;

import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXTextField;

import fr.worknshare.tickets.Config;
import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.networking.HttpMethod;
import fr.worknshare.tickets.networking.RequestCallback;
import fr.worknshare.tickets.networking.RestRequest;
import fr.worknshare.tickets.networking.RestResponse;
import fr.worknshare.tickets.repository.EmployeeRepository;
import javafx.animation.FadeTransition;
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
public class AuthController extends Controller implements RequestController {

	private EmployeeRepository employeeRepository;
	private Employee employee;

	@FXML private JFXButton submit;
	@FXML private JFXTextField emailField;
	@FXML private JFXPasswordField passwordField;

	@FXML private Label errorsEmail;
	@FXML private Label errorsPassword;

	@FXML private FlowPane loginPane;

	private Runnable loginCallback;
	private Runnable logoutCallback;

	private HttpClient httpClient;
	private HttpContext httpContext;

	@FXML
	private void initialize() {

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
	public void attempt(String email, String password) {

		RestRequest request = new RestRequest(httpClient, getUrl() + "login")
				.context(httpContext)
				.param("email", email)
				.param("password", password);

		request.asyncExecute(HttpMethod.POST, new RequestCallback() {

			@Override
			public void run() {
				RestResponse response = getResponse();
				if(response != null) {
					if(response.getStatus() == 200)
						handleAttemptSuccessResponse(response);
					else
						handleAttemptFailedResponse(response);

				} else
					Logger.getGlobal().log(Level.WARNING, "Login request failed. Internal error.");

				emailField.setDisable(false);
				passwordField.setDisable(false);
				submit.setDisable(false);
			}

		});
	}
	
	private void handleAttemptSuccessResponse(RestResponse response) {
		JsonObject payload = response.getJsonObject();
		JsonElement data = payload.get("data");
		if(data != null && data.isJsonObject()) {
			employee = employeeRepository.parseObject(data.getAsJsonObject());
			hideLoginPane();
			if(loginCallback != null) loginCallback.run();

			return;
		} else {
			//Malformed response
			getSnackbar().enqueue(new SnackbarEvent("Erreur : Réponse malformée", "error"));
			Logger.getGlobal().log(Level.INFO, "Login response malformed:\n" + response.getRaw());
		}
	}
	
	private void handleAttemptFailedResponse(RestResponse response) {
		
		if(response.getStatus() == 422) //Invalid credentials
			handleResponse(response.getJsonObject().get("errors").getAsJsonObject());
		else if(response.getStatus() == 423 || response.getStatus() == 403)
			getSnackbar().enqueue(new SnackbarEvent(response.getJsonObject().get("errors").getAsJsonObject().get("email").getAsString(), "error"));
		else if(response.getStatus() != -1) {
			JsonElement element = response.getJsonObject().get("error");
			if(element != null && element.isJsonPrimitive()) {
				getSnackbar().enqueue(new SnackbarEvent(response.getStatus() + " : " + element.getAsString(), "error"));
				Logger.getGlobal().log(Level.SEVERE, "Login request failed.\n\tStatus code " + response.getStatus() + "\n\tMessage: " + element.getAsString());
			} else {
				getSnackbar().enqueue(new SnackbarEvent("Erreur " + response.getStatus(), "error"));
				Logger.getGlobal().log(Level.SEVERE, "Login request failed.\n\tStatus code " + response.getStatus());
			}
		} else {
			Logger.getGlobal().log(Level.WARNING, "Login request failed. Remote host unreachable.");
			getSnackbar().enqueue(new SnackbarEvent("Impossible de joindre le serveur distant.", "error"));
		}
	}

	/**
	 * Send a logout request. Sets the previously authenticated employee to null.
	 */
	public void logout() {
		logout(null);
	}


	/**
	 * Send a logout request. Sets the previous authenticated employee to null.
	 * @param button - the button to disable until the request is done, nullable
	 */
	public void logout(final JFXButton button) {
		RestRequest request = new RestRequest(httpClient, getUrl() + "logout").context(httpContext);

		if(button != null) button.setDisable(true);
		request.asyncExecute(HttpMethod.POST, new RequestCallback() {

			@Override
			public void run() {
				RestResponse response = getResponse();
				if(response != null)  {
					if(response.getStatus() == 200) {
						employee = null;
						showLoginPane();
					} else if(response.getStatus() != -1) {
						JsonElement element = response.getJsonObject().get("error");
						if(element != null && element.isJsonPrimitive()) {
							getSnackbar().enqueue(new SnackbarEvent("La requête de déconnexion a échoué.\n" + response.getStatus() + " : " + element.getAsString(), "error"));
							Logger.getGlobal().log(Level.SEVERE, "Logout request failed.\n\tStatus code " + response.getStatus() + "\n\tMessage: " + element.getAsString());
						} else {
							getSnackbar().enqueue(new SnackbarEvent("La requête de déconnexion a échoué.\nErreur " + response.getStatus(), "error"));
							Logger.getGlobal().log(Level.SEVERE, "Logout request failed.\n\tStatus code " + response.getStatus());
						}
					} else {
						Logger.getGlobal().log(Level.WARNING, "Logout request failed. Remote host unreachable.");
					}
				} else
					Logger.getGlobal().log(Level.SEVERE, "Logout request failed. Internal error.");

				if(button != null) button.setDisable(false);
			}

		});
	}

	@FXML
	public void submitClicked() {
		submit();
	}

	private void submit() {
		resetInputs();
		attempt(emailField.getText(), passwordField.getText());
	}

	private void resetInputs() {
		emailField.setDisable(true);
		passwordField.setDisable(true);
		submit.setDisable(true);
		emailField.getStyleClass().remove("has-error");
		passwordField.getStyleClass().remove("has-error");
		errorsEmail.setText("");
		errorsPassword.setText("");
		errorsEmail.setVisible(false);
		errorsPassword.setVisible(false);
	}

	public void showLoginPane() {
		FadeTransition ft = new FadeTransition(Duration.millis(800), loginPane);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		loginPane.toFront();
		ft.play();
		if(logoutCallback != null) 
			ft.setOnFinished((event) -> {
				logoutCallback.run();
			});
	}

	public void hideLoginPane() {
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
			submit.setDisable(false);
		});
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
		return employee;
	}

	/**
	 * Set the behavior when the user successfully logged in
	 * @param runnable
	 */
	public void setOnLogin(Runnable runnable) {
		loginCallback = runnable;
	}

	/**
	 * Set the behavior when the user successfully logs out
	 * @param runnable
	 */
	public void setOnLogout(Runnable runnable) {
		logoutCallback = runnable;
	}

	/**
	 * Set the Http client used for auth requests
	 * @param client
	 */
	@Override
	public void setHttpClient(HttpClient client) {
		httpClient = client;
		employeeRepository.setHttpClient(httpClient);
	}

	/**
	 * Set the Http context used for auth requests
	 * @param context
	 */
	@Override
	public void setHttpContext(HttpContext context) {
		httpContext = context;
		employeeRepository.setHttpContext(httpContext);
	}

	public void setEmployeeRepository(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	public EmployeeRepository getEmployeeRepository() {
		return employeeRepository;
	}
}
