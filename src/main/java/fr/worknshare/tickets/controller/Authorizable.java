package fr.worknshare.tickets.controller;

/**
 * Interface used on Controllers needing to disable some features based on the user's role.
 * @author Jérémy LAMBERT
 *
 */
interface Authorizable {

	/**
	 * Update the components based on the current user's role.<br>
	 * The role can be get using "AuthController.getEmployee().getRole().get()"
	 * 
	 * @param role - the role (from 0 to 4) of the current user
	 */
	void updateAuthorizations(int role);

}
