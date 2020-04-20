package com.jtylerboylan.marketplace.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.jtylerboylan.marketplace.guis.UserGUI;

public class ClickGUIEvent implements Listener {

	@EventHandler
	public void onGUIClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			if (UserGUI.getUserViewing(player) != null) {
				e.setCancelled(true);
				if (e.getClick() == ClickType.RIGHT)
					UserGUI.getUserViewing(player).clickSlot(e.getSlot());
				if (e.getClick() == ClickType.LEFT)
					UserGUI.getUserViewing(player).punchSlot(e.getSlot());
			}
		}
	}
}
