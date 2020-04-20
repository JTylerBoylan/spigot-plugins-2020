package com.jtylerboylan.marketplace.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Entity;

public class BusinessEditGUI extends UserGUI {

	private Business business;
	
	public BusinessEditGUI(Player user, Business business) {
		super(user);
		this.business = business;
	}

	public void load() {

		Inventory edit_business_page = Bukkit.createInventory(user, 9, cc(business_prefix + business.getName()));
		
		ItemStack edit_name = UserGUI.tile(Material.NAME_TAG, 1, (short) 0, cc("&eEdit Name"), cc("&5Name: &7" + business.getName()), cc("&6Click to edit"));
		ItemStack add_permit = UserGUI.tile(Material.SKULL_ITEM, 1, (short) 2, cc("&aAdd Access"), cc("&2Click to add player")); 
		ItemStack remove_permit = UserGUI.tile(Material.SKULL_ITEM, 1, (short) 1, cc("&cRemove Access"), cc("&6Click to remove player")); 
		
		edit_business_page.setItem(2, edit_name);
		edit_business_page.setItem(4, add_permit);
		edit_business_page.setItem(6, remove_permit);
		edit_business_page.setItem(8, back_tile);
		
		user.openInventory(edit_business_page);
	}

	public void clickSlot(int slot) {
		switch (slot) {
		case 2:
			UserGUI.openGUI(user, new BusinessEditNameGUI(user, business));
			break;
		case 4:
			UserGUI.openGUI(user, new BusinessAddAccessGUI(user, business, 1, false));
			break;
		case 6:
			UserGUI.openGUI(user, new BusinessRemoveAccessGUI(user,business,1));
			break;
		case 8:
			UserGUI.openGUI(user, new BusinessPageGUI(user, business));
			break;
		}
		
	}

	public void stringInput(String string) {}

	public boolean hasAccess(Player player) {
		Entity entity = new Entity(player);
		if (entity.hasAccess(business))
			return true;
		return false;
	}

}
