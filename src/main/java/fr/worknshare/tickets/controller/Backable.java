package fr.worknshare.tickets.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * Interface used on Controllers having a back button, in order for them to know which panel to bring back to front.
 * @author Jérémy LAMBERT
 *
 */
interface Backable {

	/**
	 * Set the panel which will be brought to front when the "back" button is clicked
	 * @param pane
	 */
	void setBackPanel(Pane pane);

	/**
	 * What to do when the back button is clicked.
	 */
	@FXML
	void backClicked();
}
