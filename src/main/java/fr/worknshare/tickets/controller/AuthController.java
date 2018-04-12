package fr.worknshare.tickets.controller;

import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
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
	private static Employee employee;

	@FXML private JFXButton submit;
	@FXML private JFXTextField emailField;
	@FXML private JFXPasswordField passwordField;

	@FXML private Label errorsEmail;
	@FXML private Label errorsPassword;

	@FXML private FlowPane loginPane;

	private JFXSnackbar snackbar;

	private Runnable loginCallback;
	
	private static HttpClient client = HttpClientBuilder.create().build();
	private static HttpContext httpContext = new BasicHttpContext();

	@FXML
	private void initialize() {
		this.employeeRepository = new EmployeeRepository();

		//Submit on enter
		passwordField.setOnKeyPressed((event) -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				submit();
			}
		});

		CookieStore cookieStore = new BasicCookieStore();		
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		
		attempt("admin@worknshare.fr", "password"); //TODO disable auto auth
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

		RestRequest request = new RestRequest(getUrl() + "login")
				.param("email", email)
				.param("password", password);

		request.asyncExecute(HttpMethod.POST, new RequestCallback() {

			@Override
			public void run() {
				RestResponse response = getResponse();
				if(response != null) {
					if(response.getStatus() == 200) {
						JsonObject payload = response.getJsonObject();
						JsonElement data = payload.get("data");
						if(data != null && data.isJsonObject()) {
							employee = employeeRepository.parseObject(data.getAsJsonObject());
							hideLoginPane();
							if(loginCallback != null) loginCallback.run();

							return;
						} else {
							//Malformed response
							Logger.getGlobal().log(Level.INFO, "Login response malformed:\n" + response.getRaw());
						}
					} else {

						if(response.getStatus() == 422) //Invalid credentials
							handleResponse(response.getJsonObject().get("errors").getAsJsonObject());
						else if(response.getStatus() == 423 || response.getStatus() == 403)
							snackbar.enqueue(new SnackbarEvent(response.getJsonObject().get("errors").getAsJsonObject().get("email").getAsString()));
						else if(response.getStatus() != -1)				
							Logger.getGlobal().log(Level.WARNING, "Login request failed.\n\tStatus code " + response.getStatus() + "\n\tMessage: " + response.getJsonObject().get("message").getAsString());
						else {
							Logger.getGlobal().log(Level.WARNING, "Login request failed. Remote host unreachable.");
							snackbar.enqueue(new SnackbarEvent("Impossible de joindre le serveur distant."));
						}

					}
				} else {
					Logger.getGlobal().log(Level.WARNING, "Login request failed. Internal error.");
				}

				emailField.setDisable(false);
				passwordField.setDisable(false);
				submit.setDisable(false);
			}

		});
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
		RestRequest request = new RestRequest(getUrl() + "logout");

		if(button != null) button.setDisable(true);
		request.asyncExecute(HttpMethod.POST, new RequestCallback() {

			@Override
			public void run() {
				RestResponse response = getResponse();
				if(response != null)  {
					if(response.getStatus() == 200) {
						employee = null;
						showLoginPane();
					} else if(response.getStatus() != -1)				
						Logger.getGlobal().log(Level.WARNING, "Logout request failed.\n\tStatus code " + response.getStatus() + "\n\tMessage: " + response.getJsonObject().get("message").getAsString());
					else {
						Logger.getGlobal().log(Level.WARNING, "Logout request failed. Remote host unreachable.");
					}
				} else
					Logger.getGlobal().log(Level.WARNING, "Logout request failed. Internal error.");

				if(button != null) button.setDisable(false);
			}

		});
	}

	@FXML
	public void submitClicked(ActionEvent e) {
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

	public static Employee getEmployee() {
		return employee;
	}

	protected void setSnackbar(JFXSnackbar bar) {
		this.snackbar = bar;
	}

	/**
	 * Set the behavior when the user successfully logged in
	 * @param runnable
	 */
	public void setOnLogin(Runnable runnable) {
		loginCallback = runnable;
	}

	public static HttpClient getHttpClient() {
		return client;
	}
	
	public static HttpContext getContext() {
		return httpContext;
	}
	
}
