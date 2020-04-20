package com.jtylerboylan.marketplace.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.data.BusinessData;
import com.jtylerboylan.marketplace.guis.MarketShopGUI;
import com.jtylerboylan.marketplace.guis.UserGUI;

public class MarketOpenEvent implements Listener {

	private static String prefix = Core.getServerConfig().getString("plugin-prefix");
	
	@EventHandler
	public void onMarketClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (e.getClickedBlock() == null)
			return;
		if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN && !player.isSneaking()) {
			Sign sign = (Sign) e.getClickedBlock().getState();
			if (ChatColor.stripColor(sign.getLine(0)).equals("[Market]") && sign.getLine(3) != null) {
				e.setCancelled(true);
				Business business = BusinessData.getBusiness(ChatColor.stripColor(sign.getLine(3)));
				if (business == null) return;
				UserGUI.openGUI(player, new MarketShopGUI(player, business, 1));
				player.sendMessage(cc(prefix + " &aMarket > Loading..."));
			}
		}
	}
	
	private String cc(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
}
