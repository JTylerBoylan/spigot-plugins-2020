package com.jtylerboylan.marketplace.guis;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jtylerboylan.marketplace.Entity;

public class EntityEditNameGUI extends UserGUI {

	private Entity entity;
	
	public EntityEditNameGUI(Player user, Entity entity) {
		super(user);
		this.entity = entity;
	}
	
	public void load() {
		user.closeInventory();
		user.sendMessage(cc(prefix + " &e&lENTER THE NAME OF THE ENTITY " + prefix));
		user.sendMessage(cc(prefix + " &7&oCurrent Name: &9&o" + entity.getName()));
	}

	public void stringInput(String string) {
		if (string.length() > 16) {
			user.sendMessage(cc(prefix + " &cError > Name cannot be longer than 16 characters! Try Again"));
			return;
		}
		entity.setName(string);
		user.sendMessage(cc(prefix + " &aEntity > Name was successfully changed to " + entity.getName()));
		UserGUI.openGUI(user, new EntityEditGUI(user, entity));
	}

	public boolean hasAccess(Player player) {
		Entity userEntity = new Entity(player);
		if (userEntity.hasAccess(entity))
			return true;
		return false;
	}
	
	public void clickSlot(int slot) {}
	
	public void itemClicked(ItemStack clicked) {}

}
