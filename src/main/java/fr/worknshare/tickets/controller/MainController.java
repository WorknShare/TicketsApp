package fr.worknshare.tickets.controller;

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
	
	@FXML
	private void initialize() {
		snackbar = new JFXSnackbar(pane);
		snackbar.getChildren().get(0).getStyleClass().add("error");
		loginController.setSnackbar(snackbar);
	}
	
	@FXML
	public void logoutClicked(ActionEvent e) {
		loginController.logout(logout);
	}
}
