package com.jtylerboylan.marketplace.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.jtylerboylan.marketplace.Entity;

public class EntityPageGUI extends UserGUI {

	private Entity entity;
	
	public EntityPageGUI(Player user, Entity entity) {
		super(user);
		this.entity = entity;
	}
	
	public void load() {
		
		Inventory entity_page = Bukkit.createInventory(user, 9, cc(entity_prefix + entity.getName()));
		
		ItemStack info_tile = UserGUI.tile(Material.APPLE, 1, (short) 0, cc(entity_prefix + entity.getName()), cc("&2ID: &7" + entity.getID()));
		ItemStack business_tile = UserGUI.tile(Material.BOOK, 1, (short) 0, cc("&bBusiness's"), cc("&5Click to view")); 
		ItemStack edit_tile = UserGUI.tile(Material.COAL, 1, (short) 0, cc("&eEdit Entity"), cc("&6Click to edit"));
		
		entity_page.setItem(2, info_tile);
		entity_page.setItem(4, business_tile);
		entity_page.setItem(6, edit_tile);
		
		user.openInventory(entity_page);
	}

	public void clickSlot(int slot) {
		switch(slot) {
		case 4:
			UserGUI.openGUI(user, new EntityBusinessGUI(user, entity));
			break;
		case 6:
			UserGUI.openGUI(user, new EntityEditGUI(user, entity));
			break;
		}
	}

	public boolean hasAccess(Player player) {
		Entity userEntity = new Entity(player);
		if (userEntity.hasAccess(entity))
			return true;
		return false;
	}

	public void stringInput(String string) {}
}
