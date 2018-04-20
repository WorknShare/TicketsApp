package fr.worknshare.tickets.controller;

import com.jfoenix.controls.JFXSnackbar;

/**
 * Super-class for controllers. Controllers handle view inputs, process them and updates the view in response.
 * @author Jérémy LAMBERT
 *
 */
public abstract class Controller {

	private JFXSnackbar snackbar;

	/**
	 * Set the snackbar used to display errors and successes
	 * @param snackbar
	 */
	protected void setSnackbar(JFXSnackbar snackbar) {
		this.snackbar = snackbar;
	}

	/**
	 * Get the snackbar used to display errors and successes
	 * @return the snackbar
	 * @see JFXSnackbar
	 */
	public JFXSnackbar getSnackbar() {
		return snackbar;
	}

}
