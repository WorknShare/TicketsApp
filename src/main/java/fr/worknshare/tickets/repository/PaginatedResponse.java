package fr.worknshare.tickets.repository;

import java.util.ArrayList;

import fr.worknshare.tickets.view.Paginator;

/**
 * A container for a Paginator and an array of items.
 * @author Jérémy LAMBERT
 * 
 * @see Paginator
 */
public class PaginatedResponse<T> {

	private Paginator paginator;
	private ArrayList<T> items;

	public PaginatedResponse(Paginator paginator, ArrayList<T> items) {
		this.paginator = paginator;
		this.items = items;
	}

	public Paginator getPaginator() {
		return paginator;
	}
	public ArrayList<T> getItems() {
		return items;
	}



}
