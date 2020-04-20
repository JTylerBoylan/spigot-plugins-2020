package com.jtylerboylan.marketplace.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Entity;
import com.jtylerboylan.marketplace.data.BusinessData;

public class EntityBusinessGUI extends UserGUI {

	private Entity entity;
	
	private List<Business> business_list;
	
	private int inventory_size;
	
	public EntityBusinessGUI(Player user, Entity entity) {
		super(user);
		this.entity = entity;
		this.business_list = new ArrayList<Business>();
		for (Business business : BusinessData.allBusiness()) {
			if (entity.hasAccess(business))
				business_list.add(business);
		}
	}

	public void load() {
		
		inventory_size = (business_list.size() + 1) + (9-(business_list.size()+1)%9);
		
		Inventory entity_business_page = Bukkit.createInventory(user, inventory_size, cc(entity_prefix + entity.getName()));
		
		int slot = 0;
		for (Business business : business_list) {
			ItemStack tile = UserGUI.tile(Material.BOOK, 1, (short) 0, cc("&b" + business.getName()), cc("&9ID: &7" + business.getID()), cc("&7Owned by " + business.getOwner().getName()));
			entity_business_page.setItem(slot, tile);
			slot++;
		}
		
		entity_business_page.setItem(inventory_size - 1, back_tile);
		
		user.openInventory(entity_business_page);
	}

	public void clickSlot(int slot) {
		if (slot < business_list.size()) {
			UserGUI.openGUI(user, new BusinessPageGUI(user, business_list.get(slot)));
		}
		if (slot == inventory_size-1) {
			UserGUI.openGUI(user, new EntityPageGUI(user, entity));
		}
	}

	public boolean hasAccess(Player player) {
		Entity userEntity = new Entity(player);
		if (userEntity.hasAccess(entity))
			return true;
		return false;
	}
	
	public void stringInput(String string) {}
	public void itemClicked(ItemStack clicked) {}

}
