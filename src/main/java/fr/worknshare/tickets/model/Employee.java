package fr.worknshare.tickets.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public final class Employee extends Model<Employee> {

	private String token;
	private SimpleStringProperty name;
	private SimpleStringProperty surname;
	private SimpleStringProperty email;
	private SimpleStringProperty phone;
	private SimpleStringProperty address;
	private SimpleIntegerProperty role;
	private SimpleStringProperty fullName;

	public Employee(int id) {
		super(id);
		name 	= new SimpleStringProperty();
		surname = new SimpleStringProperty();
		email 	= new SimpleStringProperty();
		phone 	= new SimpleStringProperty();
		address = new SimpleStringProperty();
		role	= new SimpleIntegerProperty();
		fullName= new SimpleStringProperty();
	}

	public final SimpleStringProperty getName() {
		return name;
	}

	public final void setName(String name) {
		this.name.set(name);
		this.fullName.set((surname != null ? surname.get() : " ") + name);
	}

	public final SimpleStringProperty getSurname() {
		return surname;
	}

	public final void setSurname(String surname) {
		this.surname.set(surname);
		this.fullName.set(surname + (name != null ? " " + name.get() : ""));
	}

	public final SimpleStringProperty getEmail() {
		return email;
	}

	public final void setEmail(String email) {
		this.email.set(email);
	}

	public final SimpleStringProperty getPhone() {
		return phone;
	}

	public final void setPhone(String phone) {
		this.phone.set(phone);
	}

	public final SimpleStringProperty getAddress() {
		return address;
	}

	public final void setAddress(String address) {
		this.address.set(address);
	}

	public final SimpleIntegerProperty getRole() {
		return role;
	}

	public final void setRole(int role) {
		this.role.set(role);
	}

	public final String getToken() {
		return token;
	}

	public final void setToken(String token) {
		this.token = token;
	}
	
	public final SimpleStringProperty getFullName() {
		return fullName;
	}
	
	public String toString() {
		return surname.get() + " " + name.get();
	}

}
