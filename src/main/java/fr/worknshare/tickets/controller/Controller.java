package fr.worknshare.tickets.controller;

import com.jfoenix.controls.JFXSnackbar;

public abstract class Controller {

	private JFXSnackbar snackbar;
	
	/**
	 * Set the snackbar used to display errors
	 * @param snackbar
	 */
	protected void setSnackbar(JFXSnackbar snackbar) {
		this.snackbar = snackbar;
	}
	
	public JFXSnackbar getSnackbar() {
		return snackbar;
	}
	
}
