package com.jtylerboylan.marketplace.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.jtylerboylan.marketplace.guis.UserGUI;

public class ChatGUIEvent implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		if (UserGUI.getUserViewing(player) != null) {
			e.setCancelled(true);
			UserGUI.getUserViewing(player).stringInput(e.getMessage());
		}
	}
	
}
