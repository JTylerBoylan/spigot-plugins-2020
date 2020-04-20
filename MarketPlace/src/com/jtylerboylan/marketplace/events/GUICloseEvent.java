package com.jtylerboylan.marketplace.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.jtylerboylan.marketplace.guis.UserGUI;

public class GUICloseEvent implements Listener {

	@EventHandler
	public void onClose(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		if (UserGUI.getUserViewing(p) != null) {
			UserGUI.setUserViewing(p, null);
		}
	}
	
}
