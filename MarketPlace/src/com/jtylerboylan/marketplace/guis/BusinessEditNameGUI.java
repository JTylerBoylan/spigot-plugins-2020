package com.jtylerboylan.marketplace.guis;

import org.bukkit.entity.Player;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Entity;

public class BusinessEditNameGUI extends UserGUI {

	private Business business;
	
	public BusinessEditNameGUI(Player user, Business business) {
		super(user);
		this.business = business;
	}

	public void load() {
		user.closeInventory();
		user.sendMessage(cc(prefix + " &e&lENTER THE NAME OF THE ENTITY " + prefix));
		user.sendMessage(cc(prefix + " &7&oCurrent Name: &9&o" + business.getName()));
	}

	public void clickSlot(int slot) {}

	public void stringInput(String string) {
		if (string.length() > 16) {
			user.sendMessage(cc(prefix + " &cError > Name cannot be longer than 16 characters! Try Again"));
			return;
		}
		business.setName(string);
		user.sendMessage(cc(prefix + " &aBusiness > Name was successfully changed to " + business.getName()));
		UserGUI.openGUI(user, new BusinessEditGUI(user, business));
	}

	public boolean hasAccess(Player player) {
		Entity entity = new Entity(player);
		if (entity.hasAccess(business))
			return true;
		return false;
	}

}
