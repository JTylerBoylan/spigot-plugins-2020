package com.jtylerboylan.marketplace.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.jtylerboylan.marketplace.Entity;

public class EntityEditGUI extends UserGUI {

	private Entity entity;
	
	public EntityEditGUI(Player user, Entity entity) {
		super(user);
		this.entity = entity;
	}

	public void load() {
		
		Inventory edit_entity_page = Bukkit.createInventory(user, 9, cc(entity_prefix + entity.getName()));
		
		ItemStack edit_name = UserGUI.tile(Material.NAME_TAG, 1, (short) 0, cc("&eEdit Name"), cc("&5Name: &7" + entity.getName()), cc("&6Click to edit"));
		ItemStack add_permit = UserGUI.tile(Material.SKULL_ITEM, 1, (short) 2, cc("&aAdd Access"), cc("&2Click to add player")); 
		ItemStack remove_permit = UserGUI.tile(Material.SKULL_ITEM, 1, (short) 1, cc("&cRemove Access"), cc("&6Click to remove player")); 
		
		edit_entity_page.setItem(2, edit_name);
		edit_entity_page.setItem(4, add_permit);
		edit_entity_page.setItem(6, remove_permit);
		edit_entity_page.setItem(8, back_tile);
		
		user.openInventory(edit_entity_page);
	}

	public void clickSlot(int slot) {
		switch (slot) {
		case 2:
			UserGUI.openGUI(user, new EntityEditNameGUI(user, entity));
			break;
		case 4:
			UserGUI.openGUI(user, new EntityAddAccessGUI(user, entity, 1, false));
			break;
		case 6:
			UserGUI.openGUI(user, new EntityRemoveAccessGUI(user,entity,1));
			break;
		case 8:
			UserGUI.openGUI(user, new EntityPageGUI(user, entity));
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

	public void itemClicked(ItemStack clicked) {}
}
