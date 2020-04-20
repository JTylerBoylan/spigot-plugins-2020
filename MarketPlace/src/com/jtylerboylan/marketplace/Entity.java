package com.jtylerboylan.marketplace;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.jtylerboylan.marketplace.data.EntityData;

public class Entity {
	
	private String entity_id;
	
	private String display_name;
	
	private List<String> permit_list;
	
	private boolean override;
	
	private boolean is_custom;
	
	public Entity(String entity_id) {
		this.entity_id = entity_id;
		this.display_name = entity_id;
		this.permit_list = new ArrayList<String>();
		this.is_custom = true;
		this.override = false;
	}
	
	public Entity(String entity_id, String display_name, List<String> permit_list, boolean override) {
		this.entity_id = entity_id;
		this.display_name = display_name;
		this.permit_list = permit_list;
		this.is_custom = true;
		this.override = override;
	}
	
	public Entity(Player player) {
		this.entity_id = player.getUniqueId().toString();
		this.display_name = player.getName();
		permit_list = new ArrayList<String>();
		this.is_custom = false;
		this.override = player.hasPermission("market.override");
	}
	
	public String getID() {
		return entity_id;
	}
	
	public void setName(String name) {
		display_name = name;
	}
	
	public String getName() {
		return display_name;
	}
	
	public List<String> getPermitted(){
		return permit_list;
	}
	
	public boolean isCustom() {
		return is_custom;
	}
	
	public void permit(String entity_id) {
		permit_list.add(entity_id);
	}
	public void unpermit(String entity_id) {
		permit_list.remove(entity_id);
	}
	
	public boolean hasAccess(Market market) {
		if (override) return true;
		return market.getPermitted().contains(entity_id) || hasAccess(market.getOwner()) || market.getOwner().getID().equals(entity_id);
	}
	public boolean hasAccess(Business business) {
		if (override) return true;
		for (String s : business.getPermitted())
			if (this.hasAccess(EntityData.getEntity(s)))
				return true;
		return business.getPermitted().contains(entity_id) || hasAccess(business.getOwner()) || business.getOwner().getID().equals(entity_id);
	}
	public boolean hasAccess(Entity entity) {
		if (override) return true;
		return entity.getPermitted().contains(entity_id);
	}
	
	public boolean override() {
		return override;
	}
}
