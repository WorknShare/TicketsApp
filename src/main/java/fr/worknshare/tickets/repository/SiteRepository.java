package fr.worknshare.tickets.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.Equipment;
import fr.worknshare.tickets.model.EquipmentType;
import fr.worknshare.tickets.model.Site;
import fr.worknshare.tickets.view.Paginator;

public class SiteRepository extends Repository<Site>{

	@Override
	public String getResourceName() {
		return "equipmenttype";
	}

	@Override
	public Site parseObject(JsonObject object) {

		JsonElement element = object.get("id_site");
		if(element != null && element.isJsonPrimitive()) {
			Site site = new Site(element.getAsInt());

			//Name
			element = object.get("name");
			if(element != null && element.isJsonPrimitive()) site.setName(element.getAsString());
			
			//Address
			element = object.get("address");
			if(element != null && element.isJsonPrimitive()) site.setName(element.getAsString());

			return site;
		}
		return null;
	}
}
