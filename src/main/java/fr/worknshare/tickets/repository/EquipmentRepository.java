package fr.worknshare.tickets.repository;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.SimpleStringProperty;

import fr.worknshare.tickets.model.Equipment;

public final class EquipmentRepository extends Repository<Equipment> {

	private EquipmentTypeRepository equipmentTypeRepository;
	private SiteRepository siteRepository;


	public EquipmentRepository() {
		super();
		equipmentTypeRepository = new EquipmentTypeRepository(this);
		siteRepository = new SiteRepository();
	}

	public EquipmentRepository(EquipmentTypeRepository equipmentTypeRepository) {
		super();
		this.equipmentTypeRepository = equipmentTypeRepository;
	}

	@Override
	public String getResourceName() {
		return "equipmenttype/equipment";
	}

	@Override
	public Equipment parseObject(JsonObject object) {

		JsonElement element = object.get("id_equipment");
		if(element != null && element.isJsonPrimitive()) {
			Equipment equipment = new Equipment(element.getAsInt());

			//Serial number
			element = object.get("serial_number");
			if(element != null && element.isJsonPrimitive()) equipment.setName(element.getAsString());

			//Type id
			element = object.get("id_equipment_type");
			if(element != null && element.isJsonPrimitive()) equipment.setEquipmentTypeId(element.getAsInt());

			//Type
			element = object.get("type");
			if(element != null && element.isJsonObject()) equipment.setEquipmentType(equipmentTypeRepository.parseObject(element.getAsJsonObject()));

			//Site
			element = object.get("site");
			if(element != null && element.isJsonObject()) equipment.setSite(siteRepository.parseObject(element.getAsJsonObject()));

			return equipment;

		}
		return null;
	}

}
