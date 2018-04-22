package fr.worknshare.tickets.model;

import javafx.beans.property.SimpleStringProperty;

public class Site extends Model<Site>{

	private SimpleStringProperty name;
	private SimpleStringProperty address;
	
	
	public Site(int id) {
		super(id);
		name 	= new SimpleStringProperty();
		address = new SimpleStringProperty();
	}

	public final SimpleStringProperty getName() {
		return name;
	}

	public final void setName(String name) {
		this.name.set(name);
	}
	

	public final SimpleStringProperty getAdress() {
		return name;
	}

	public final void setAddress(String address) {
		this.address.set(address);
	}
}
