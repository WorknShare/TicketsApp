package fr.worknshare.tickets.repository;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.Equipment;

public final class EquipmentRepository extends Repository<Equipment> {

	private EquipmentTypeRepository equipmentTypeRepository;

	public EquipmentRepository(HttpClient client, HttpContext context) {
		super(client, context);
		equipmentTypeRepository = new EquipmentTypeRepository(client, context, this);
	}

	public EquipmentRepository(HttpClient client, HttpContext context, EquipmentTypeRepository equipmentTypeRepository) {
		super(client, context);
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

			Equipment equipment = getFromCache(element.getAsInt());
			if(equipment == null)			
				equipment = new Equipment(element.getAsInt());

			//Serial number
			element = object.get("serial_number");
			if(element != null && element.isJsonPrimitive()) equipment.setName(element.getAsString());

			//Type id
			element = object.get("id_equipment_type");
			if(element != null && element.isJsonPrimitive()) equipment.setEquipmentTypeId(element.getAsInt());

			//Type
			element = object.get("type");
			if(element != null && element.isJsonObject()) equipment.setEquipmentType(equipmentTypeRepository.parseObject(element.getAsJsonObject()));

			registerModel(equipment);
			return equipment;
		}
		return null;
	}

	public EquipmentTypeRepository getEquipmentTypeRepository() {
		return equipmentTypeRepository;
	}

}
