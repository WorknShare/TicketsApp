package fr.worknshare.tickets.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public final class Employee extends Model<Employee> {

	private SimpleStringProperty name;
	private SimpleStringProperty surname;
	private SimpleStringProperty email;
	private SimpleStringProperty phone;
	private SimpleStringProperty address;
	private SimpleIntegerProperty role;
	
	public Employee(int id) {
		super(id);
	}

	public final SimpleStringProperty getName() {
		return name;
	}
	
	public final void setName(SimpleStringProperty name) {
		this.name = name;
	}
	
	public final SimpleStringProperty getSurname() {
		return surname;
	}
	
	public final void setSurname(SimpleStringProperty surname) {
		this.surname = surname;
	}
	
	public final SimpleStringProperty getEmail() {
		return email;
	}
	
	public final void setEmail(SimpleStringProperty email) {
		this.email = email;
	}
	
	public final SimpleStringProperty getPhone() {
		return phone;
	}
	
	public final void setPhone(SimpleStringProperty phone) {
		this.phone = phone;
	}
	
	public final SimpleStringProperty getAddress() {
		return address;
	}
	
	public final void setAddress(SimpleStringProperty address) {
		this.address = address;
	}
	
	public final SimpleIntegerProperty getRole() {
		return role;
	}
	
	public final void setRole(SimpleIntegerProperty role) {
		this.role = role;
	}
	
	
	
}
