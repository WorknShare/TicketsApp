package fr.worknshare.tickets.model;

import javafx.beans.property.SimpleStringProperty;

public final class Equipment extends Model<Equipment> {

	private SimpleStringProperty name;
	private EquipmentType equipmentType;

	public Equipment(int id) {
		super(id);
		name 	= new SimpleStringProperty();
	}

	public final SimpleStringProperty getName() {
		return name;
	}

	public final void setName(String name) {
		this.name.set(name);
	}

	public EquipmentType getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(EquipmentType equipmentType) {
		this.equipmentType = equipmentType;
	}

}
