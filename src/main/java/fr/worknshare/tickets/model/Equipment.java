package fr.worknshare.tickets.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public final class Equipment extends Model<Equipment> {

	private SimpleStringProperty name;
	private SimpleIntegerProperty equipmentTypeId;
	private EquipmentType equipmentType;

	public Equipment(int id) {
		super(id);
		name 			= new SimpleStringProperty();
		equipmentTypeId = new SimpleIntegerProperty();
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

	public final SimpleIntegerProperty getEquipmentTypeId() {
		return equipmentTypeId;
	}

	public final void setEquipmentTypeId(int id) {
		this.equipmentTypeId.set(id);
	}

}
