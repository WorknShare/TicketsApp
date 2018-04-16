package fr.worknshare.tickets.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.EquipmentType;
import fr.worknshare.tickets.view.Paginator;

public final class EquipmentTypeRepository extends Repository<EquipmentType> {

	private EquipmentRepository equipmentRepository;

	public EquipmentTypeRepository() {
		super();
		equipmentRepository = new EquipmentRepository();
	}
	
	public EquipmentTypeRepository(EquipmentRepository equipmentRepository) {
		super();
		this.equipmentRepository = equipmentRepository;
	}

	@Override
	public String getResourceName() {
		return "equipmenttype";
	}

	@Override
	public EquipmentType parseObject(JsonObject object) {

		JsonElement element = object.get("id_equipment_type");
		if(element != null && element.isJsonPrimitive()) {
			EquipmentType equipmentType = new EquipmentType(element.getAsInt());

			//Name
			element = object.get("name");
			if(element != null && element.isJsonPrimitive()) equipmentType.setName(element.getAsString());
			
			//Equipment
			element = object.get("equipment");
			if(element != null && element.isJsonArray()) {
				JsonArray array = element.getAsJsonArray();
				array.forEach((elem) -> {
					if(elem.isJsonObject())
						equipmentType.addEquipment(equipmentRepository.parseObject(elem.getAsJsonObject()));
				});
			}
			
			//Paginator
			element = object.get("paginator");
			if(element != null && element.isJsonObject()) equipmentType.setPaginator(Paginator.fromJson(element.getAsJsonObject()));

			return equipmentType;
		}
		return null;
	}

}
