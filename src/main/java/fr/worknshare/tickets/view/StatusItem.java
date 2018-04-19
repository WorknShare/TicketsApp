package fr.worknshare.tickets.view;

public final class StatusItem {

	private int status;
	private String name;

	public StatusItem(int status, String name) {
		this.status = status;
		this.name = name;
	}

	public final int getStatus() {
		return status;
	}

	public final String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

}
