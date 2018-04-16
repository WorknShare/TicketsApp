package fr.worknshare.tickets.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public final class Ticket extends Model<Ticket> {

	private SimpleIntegerProperty status;
	private SimpleStringProperty description;
	private SimpleStringProperty createdAt;
	private SimpleStringProperty updatedAt;

	private SimpleIntegerProperty idEquipment;
	private Equipment equipment;
	private EquipmentType equipmentType;

	private SimpleIntegerProperty idEmployeeSource;
	private SimpleIntegerProperty idEmployeeAssigned;
	private Employee employeeSource;
	private Employee employeeAssigned;

	private SimpleDateFormat dateFormatter;

	public Ticket(int id) {
		super(id);
		status			   = new SimpleIntegerProperty();
		idEmployeeSource   = new SimpleIntegerProperty();
		idEmployeeAssigned = new SimpleIntegerProperty();
		description 	   = new SimpleStringProperty();
		createdAt		   = new SimpleStringProperty();
		updatedAt		   = new SimpleStringProperty();
		dateFormatter 	   = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	}

	public final SimpleIntegerProperty getStatus() {
		return status;
	}

	public final void setStatus(int status) {
		this.status.set(status);
	}

	public final SimpleStringProperty getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description.set(description);
	}

	public final SimpleStringProperty getCreatedAt() {
		return createdAt;
	}

	public final void setCreatedAt(Date createdAt) {
		this.createdAt.set(dateFormatter.format(createdAt));
	}

	public final SimpleStringProperty getUpdatedAt() {
		return updatedAt;
	}

	public final void setUpdatedAt(Date updatedAt) {
		this.updatedAt.set(dateFormatter.format(updatedAt));
	}

	public final Employee getEmployeeSource() {
		return employeeSource;
	}

	public final void setEmployeeSource(Employee employeeSource) {
		this.employeeSource = employeeSource;
	}

	public final Employee getEmployeeAssigned() {
		return employeeAssigned;
	}

	public final void setEmployeeAssigned(Employee employeeAssigned) {
		this.employeeAssigned = employeeAssigned;
	}

	public final SimpleIntegerProperty getIdEmployeeSource() {
		return idEmployeeSource;
	}

	public final void setIdEmployeeSource(int idEmployeeSource) {
		this.idEmployeeSource.set(idEmployeeSource);
	}

	public final SimpleIntegerProperty getIdEmployeeAssigned() {
		return idEmployeeAssigned;
	}

	public final void setIdEmployeeAssigned(int idEmployeeAssigned) {
		this.idEmployeeAssigned.set(idEmployeeAssigned);;
	}

	public final Equipment getEquipment() {
		return equipment;
	}

	public final void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public final EquipmentType getEquipmentType() {
		return equipmentType;
	}

	public final void setEquipmentType(EquipmentType equipmentType) {
		this.equipmentType = equipmentType;
	}

	public final SimpleIntegerProperty getIdEquipment() {
		return idEquipment;
	}

	public final void setIdEquipment(int idEquipment) {
		this.idEquipment.set(idEquipment);;
	}

}
