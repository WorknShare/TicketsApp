package fr.worknshare.tickets.model;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;

public final class EquipmentType extends Model<EquipmentType> {

	private SimpleStringProperty name;
	private ArrayList<Equipment> equipment;

	public EquipmentType(int id) {
		super(id);
		name 	= new SimpleStringProperty();
		equipment = new ArrayList<Equipment>();
	}

	public final SimpleStringProperty getName() {
		return name;
	}

	public final void setName(String name) {
		this.name.set(name);
	}

	public ArrayList<Equipment> getEquipment() {
		return equipment;
	}

	public void addEquipment(Equipment equipment) {
		this.equipment.add(equipment);
	}

	public boolean removeEquipment(Equipment equipment) {
		return this.equipment.remove(equipment);
	}

}
