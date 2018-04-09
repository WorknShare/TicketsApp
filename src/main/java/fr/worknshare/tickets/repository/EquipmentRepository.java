package fr.worknshare.tickets.repository;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.Equipment;

public final class EquipmentRepository extends Repository<Equipment> {

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

			return equipment;
		}
		return null;
	}

}
