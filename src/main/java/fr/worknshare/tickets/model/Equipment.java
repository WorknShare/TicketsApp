package fr.worknshare.tickets.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public final class Equipment extends Model<Equipment> {

	private SimpleStringProperty name;
	private SimpleIntegerProperty equipmentTypeId;
	private EquipmentType equipmentType;
	private SimpleIntegerProperty siteId;
	private Site site;

	public Equipment(int id) {
		super(id);
		name = new SimpleStringProperty();
		equipmentTypeId = new SimpleIntegerProperty();
		siteId = new SimpleIntegerProperty();
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

	public Site getSite(){
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public final SimpleIntegerProperty getEquipmentTypeId() {
		return equipmentTypeId;
	}

	public final void setEquipmentTypeId(int id) {
		this.equipmentTypeId.set(id);
	}
	
	public final void setSiteId(int id) {
		siteId.set(id);
	}
	
	public final SimpleIntegerProperty getSiteId() {
		return siteId;
	}

}
