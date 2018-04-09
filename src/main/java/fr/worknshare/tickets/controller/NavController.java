package fr.worknshare.tickets.controller;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controller used for the navigation
 * 
 * @author Jérémy LAMBERT
 *
 */
public class NavController {
	
	@FXML private JFXButton logout;
	@FXML private AuthController loginController;

	@FXML
	public void logoutClicked(ActionEvent e) {
		loginController.logout(logout);
	}
}
