package fr.worknshare.tickets.repository;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.worknshare.tickets.model.Site;
import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.networking.RequestCallback;
import fr.worknshare.tickets.view.Paginator;

public class SiteRepository extends Repository<Site>{

	
	public SiteRepository(HttpClient client, HttpContext context) {
		super(client, context);
	}
	
	@Override
	public String getResourceName() {
		return "site";
	}
	
	@Override
	public Site parseObject(JsonObject object) {

		JsonElement element = object.get("id_site");
		if(element != null && element.isJsonPrimitive()) {
			Site site = getFromCache(element.getAsInt());
			if(site == null)
				site = new Site(element.getAsInt());
			

			//Name
			element = object.get("name");
			if(element != null && element.isJsonPrimitive()) site.setName(element.getAsString());
			
			//Address
			element = object.get("address");
			if(element != null && element.isJsonPrimitive()) site.setAddress(element.getAsString());
			registerModel(site);
			return site;
		}
		return null;
	}

}
