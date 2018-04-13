package fr.worknshare.tickets.controller;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/**
 * Controller used for the navigation
 * 
 * @author Jérémy LAMBERT
 *
 */
public class MainController {

	private JFXSnackbar snackbar;
	@FXML private JFXButton logout;
	@FXML private StackPane pane;
	@FXML private AuthController loginController;
	@FXML private TicketsController ticketsController;
	
	private HttpClient client = HttpClientBuilder.create().build();
	private HttpContext context = new BasicHttpContext();
	
	@FXML
	private void initialize() {
		snackbar = new JFXSnackbar(pane);
		snackbar.getChildren().get(0).getStyleClass().add("error");
		
		CookieStore cookieStore = new BasicCookieStore();		
		context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		
		loginController.setHttpClient(client);
		loginController.setHttpContext(context);
		loginController.attempt("admin@worknshare.fr", "password"); //TODO disable auto auth
		
		loginController.setSnackbar(snackbar);
		loginController.setOnLogin(() -> {
			ticketsController.refresh();
		});
		
		ticketsController.setHttpClient(client);
		ticketsController.setHttpContext(context);
	}
	
	@FXML
	public void logoutClicked(ActionEvent e) {
			loginController.logout(logout);
	}
	
	public HttpClient getHttpClient() {
		return client;
	}
	
	public HttpContext getContext() {
		return context;
	}
}
