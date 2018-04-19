package fr.worknshare.tickets.repository;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.EquipmentType;
import fr.worknshare.tickets.view.Paginator;

public final class EquipmentTypeRepository extends Repository<EquipmentType> {

	private EquipmentRepository equipmentRepository;


	public EquipmentTypeRepository(HttpClient client, HttpContext context) {
		super(client, context);
		equipmentRepository = new EquipmentRepository(client, context, this);
	}

	public EquipmentTypeRepository(HttpClient client, HttpContext context, EquipmentRepository equipmentRepository) {
		super(client, context);
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

			EquipmentType equipmentType = getFromCache(element.getAsInt());
			if(equipmentType == null)			
				equipmentType = new EquipmentType(element.getAsInt());

			EquipmentType finalEquipmentType = equipmentType; //Must create another effectively final variable for the forEach lambda in equipment parsing

			//Name
			element = object.get("name");
			if(element != null && element.isJsonPrimitive()) finalEquipmentType.setName(element.getAsString());

			//Equipment
			element = object.get("equipment");
			if(element != null && element.isJsonArray()) {
				JsonArray array = element.getAsJsonArray();
				array.forEach((elem) -> {
					if(elem.isJsonObject())
						finalEquipmentType.addEquipment(equipmentRepository.parseObject(elem.getAsJsonObject()));
				});
			}

			//Paginator
			element = object.get("paginator");
			if(element != null && element.isJsonObject()) finalEquipmentType.setPaginator(Paginator.fromJson(element.getAsJsonObject()));

			registerModel(finalEquipmentType);
			return finalEquipmentType;
		}
		return null;
	}

}
